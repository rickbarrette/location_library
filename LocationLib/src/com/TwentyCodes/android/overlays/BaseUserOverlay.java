/**
 * UserOverlayBase.java
 * @date Jan 12, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.overlays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import com.TwentyCodes.android.debug.Debug;
import com.TwentyCodes.android.location.CompassListener;
import com.TwentyCodes.android.location.GeoPointLocationListener;
import com.TwentyCodes.android.location.GeoUtils;
import com.TwentyCodes.android.location.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * This class will be used to build user overlays
 * @author ricky barrette
 */
public abstract class BaseUserOverlay extends Overlay implements GeoPointLocationListener, CompassListener {
	
	/**
	 * This thread is responsible for animating the user icon
	 * @author ricky barrette
	 */
	public class AnimationThread extends Thread {
		
		private boolean isAborted;

		public void abort(){
			isAborted = true;
		}

		/**
		 * Main method of this animation thread
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override 
		public void run(){
			super.run();
			int index = 0;
			boolean isCountingDown = false;
			while (true) {
				synchronized (this) {
					if (isAborted) {
						break;
					}
					
					switch(index){
					case 1:
						mUserArrow = R.drawable.user_arrow_animation_2;
						if(isCountingDown)
							index--;
						else
							index++;
						
						try {
							sleep(100l);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					case 2:
						mUserArrow = R.drawable.user_arrow_animation_3;
						index--;
						isCountingDown = true;
						try {
							sleep(200l);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					default:
						mUserArrow = R.drawable.user_arrow_animation_1;
						index++;
						isCountingDown = false;
						try {
							sleep(2000l);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
						break;
					}
				}
			}
			
		}
	}
	
	private final String TAG = "UserOverlayBase";
	private boolean isEnabled;
	private int mUserArrow = R.drawable.user_arrow_animation_1;
	private AnimationThread mAnimationThread;
	private float mBearing = 0;
	private int mAccuracy;
	private GeoPoint mPoint;
	private Context mContext;
	private MapView mMapView;
	private boolean isFistFix = true;
	private GeoPointLocationListener mListener;
	public boolean isFollowingUser = true;
	private CompasOverlay mCompass;
	private boolean isCompassEnabled;
	
	private CompassListener mCompassListener;
	
	/**
	 * Construct a new UserOverlay
	 * @param mapView
	 * @param context
	 * @author ricky barrette
	 */
	public BaseUserOverlay(MapView mapView, Context context) {
		super();
		mContext = context;
		mMapView = mapView;
		mCompass = new CompasOverlay(context);
		mUserArrow = R.drawable.user_arrow_animation_1;
	}
	
	/**
	 * Construct a new UserOverlayTODO Auto-generated method stub
	 * @param mapView
	 * @param context
	 * @param followUser
	 * @author ricky barrette
	 */
	public BaseUserOverlay(MapView mapView, Context context, boolean followUser) {
		this(mapView, context);
		isFollowingUser = followUser;
	}
	
	/**
	 * Disables the compass
	 * @author ricky barrette
	 */
	public final void disableCompass(){
		isCompassEnabled = false;
		mMapView.getOverlays().remove(mCompass);
	}
	
	/**
	 * Stops location updates and removes the overlay from view
	 * @author ricky barrette
	 */
	public final void disableMyLocation(){
		Log.d(TAG,"disableMyLocation()");
		onMyLocationDisabled();
		isEnabled = false;
		mCompass.disable();
		if(mListener != null)
			mListener.onFirstFix(false);
		mAnimationThread.abort();
	}
	
	/**
	 * we override this methods so we can provide a drawable and a location to draw on the canvas.
	 * (non-Javadoc)
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 * @param canvas
	 * @param mapView
	 * @param shadow
	 * @author ricky barrette
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow){
		if (isEnabled && mPoint != null) {
			Point center = new Point();
			Point left = new Point();
			Projection projection = mapView.getProjection();
			GeoPoint leftGeo = GeoUtils.distanceFrom(mPoint, mAccuracy);
			projection.toPixels(leftGeo, left);
			projection.toPixels(mPoint, center);
			canvas = drawAccuracyCircle(center, left, canvas);
			canvas = drawUser(center, mBearing, canvas);
			/*
			 * the following log is used to demonstrate if the leftGeo point is the correct
			 */
			if(Debug.DEBUG)
				Log.d(TAG, (GeoUtils.distanceKm(mPoint, leftGeo) * 1000)+"m");
		}
		super.draw(canvas, mapView, shadow);
	}

	/**
	 * draws an accuracy circle onto the canvas supplied
	 * @param center point of the circle
	 * @param left point of the circle
	 * @param canvas to be drawn on
	 * @return modified canvas
	 * @author ricky barrette
	 */
    private Canvas drawAccuracyCircle(Point center, Point left, Canvas canvas) {
    	Paint paint = new Paint();
    	
        /*
         * get radius of the circle being drawn by 
         */
        int circleRadius = center.x - left.x;
        if(circleRadius <= 0){
        	circleRadius = left.x - center.x;
        }
        /*
         * paint a blue circle on the map  
         */
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        paint.setColor(Color.BLUE);
        paint.setStyle(Style.STROKE);
        canvas.drawCircle(center.x, center.y, circleRadius, paint);
		/*
		 * fill the radius with a alpha blue
		 */
		paint.setAlpha(30);
        paint.setStyle(Style.FILL);
        canvas.drawCircle(center.x, center.y, circleRadius, paint);
        
        /*
         * for testing
         * draw a dot over the left geopoint 
         */
        if(Debug.DEBUG){
	        paint.setColor(Color.RED);
			RectF oval = new RectF(left.x - 1, left.y - 1, left.x + 1, left.y + 1);
			canvas.drawOval(oval, paint);
        }
    	
        return canvas;
	}
	
    /**
     * draws user arrow that points north based on bearing onto the supplied canvas
     * @param point to draw user arrow on
     * @param bearing of the device
     * @param canvas to draw on
     * @return modified canvas
     * @author ricky barrette
     */
    private Canvas drawUser(Point point, float bearing, Canvas canvas){
    	Bitmap user = BitmapFactory.decodeResource(mContext.getResources(), mUserArrow);
        Matrix matrix = new Matrix();
        matrix.postRotate(bearing);
        Bitmap rotatedBmp = Bitmap.createBitmap(
            user, 
            0, 0, 
            user.getWidth(), 
            user.getHeight(), 
            matrix, 
            true
        );
        canvas.drawBitmap(
            rotatedBmp, 
            point.x - (rotatedBmp.getWidth()  / 2), 
            point.y - (rotatedBmp.getHeight() / 2), 
            null
        );
        return canvas;
    }
    
    /**
     * Enables the compass
     * @author ricky barrette
     */
    public void enableCompass(){
    	if(! this.isCompassEnabled){
    		this.mMapView.getOverlays().add(this.mCompass);
    		this.isCompassEnabled = true;
    	}
    }
    
    /**
     * Attempts to enable MyLocation, registering for updates from provider
     * @author ricky barrette
     */
    public void enableMyLocation(){
    	if(Debug.DEBUG)
    		Log.d(TAG,"enableMyLocation()");
    	if (! isEnabled) {
    		
    		mAnimationThread = new AnimationThread();
    		mAnimationThread.start();
    		
			onMyLocationEnabled();
			isEnabled = true;
			mCompass.enable(this);
			isFistFix = true;
			if(mListener != null)
				mListener.onFirstFix(false);
		}
    }
    
    /**
     * Allows the map to follow the user
     * @param followUser
     * @author ricky barrette
     */
    public void followUser(boolean followUser){
    	if(Debug.DEBUG)
    		Log.d(TAG,"followUser()");
    	isFollowingUser = followUser;
    }
    
    /**
	 * @return return the current destination
	 * @author ricky barrette
	 */
	public GeoPoint getDestination(){
		return mCompass.getDestination();
	}
    
    /**
     * returns the users current bearing
     * @return
     * @author ricky barrette
     */
    public float getUserBearing(){
    	return mBearing;
    }
    
    /**
	 * returns the users current location
	 * @return
	 * @author ricky barrette
	 */
	public GeoPoint getUserLocation(){
		return mPoint;
	}
    
	@Override
	public void onCompassUpdate(float bearing) {
		if(mCompassListener != null)
			mCompassListener.onCompassUpdate(bearing);
		mBearing = bearing;
		mMapView.invalidate();
	}

	/**
	 * called when the SkyHook location changes, this mthod is resposiable for updating the overlay location and accuracy circle.
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.SkyHook.GeoPointLocationListener.location.LocationListener#onLocationChanged(com.google.android.maps.GeoPoint, float)
	 * @param point
	 * @param accuracy
	 * @author ricky barrette
	 */
	@Override
	public void onLocationChanged(GeoPoint point, int accuracy) {
		
		if(mCompass != null)
			mCompass.setLocation(point);
		
		/*
		 * if this is the first fix
		 * set map center the users location, and zoom to the max zoom level
		 */
		if(point != null && isFistFix){
			mMapView.getController().setCenter(point);
			mMapView.getController().setZoom( (mMapView.getMaxZoomLevel() - 2) );
			if(mListener != null)
				mListener.onFirstFix(true);
			isFistFix = false;
		}
		
		//update the users point, and accuracy for the UI
		mPoint = point;
		mAccuracy = accuracy;
		mMapView.invalidate();
		if(mListener != null){
			mListener.onLocationChanged(point, accuracy);
		}
		
		if (isFollowingUser) {
			panToUserIfOffMap(point);
		}
	}

	/**
	 * Called when disableMyLocation is called. This is where you want to disable any location updates from your provider
	 * @author ricky barrette
	 */
	public abstract void onMyLocationDisabled();

	/**
     * Called when the enableMyLocation() is called. This is where you want to ask your location provider for updates 
     * @author ricky barrette
     */
    public abstract void onMyLocationEnabled();
	
	/**
	 * pans the map view if the user is off screen.
	 * @author ricky barrette
	 */
	private void panToUserIfOffMap(GeoPoint user) {
		GeoPoint center = mMapView.getMapCenter();
		double distance = GeoUtils.distanceKm(center, user);
		double distanceLat = GeoUtils.distanceKm(center, new GeoPoint((center.getLatitudeE6() + (int) (mMapView.getLatitudeSpan() / 2)), center.getLongitudeE6()));
		double distanceLon = GeoUtils.distanceKm(center, new GeoPoint(center.getLatitudeE6(), (center.getLongitudeE6() + (int) (mMapView.getLongitudeSpan() / 2))));
		
		double whichIsGreater = (distanceLat > distanceLon) ? distanceLat : distanceLon;
		
		/**
		 * if the user is one the map, keep them their
		 * else don't pan to user unless they pan pack to them
		 */
		if( ! (distance > whichIsGreater) )
			if (distance >  distanceLat || distance > distanceLon){
				mMapView.getController().animateTo(user);
			}
	}
	
	/**
	 * Attempts to register the listener for location updates
	 * @param listener
	 * @author Ricky Barrette
	 */
	public void registerListener(GeoPointLocationListener listener){
		Log.d(TAG,"registerListener()");
		if (mListener == null){
			mListener = listener;
		}
	}

	/**
	 * Set the compass drawables and location
	 * @param needleResId
	 * @param backgroundResId
	 * @param x
	 * @param y
	 * @author ricky barrette
	 */
	public void setCompassDrawables(int needleResId, int backgroundResId, int x, int y) {
		mCompass.setDrawables(needleResId, backgroundResId, x, y);
	}

	/**
	 * Sets the CompassListener
	 * @param listener
	 * @author ricky barrette
	 */
	public void setCompassListener(CompassListener listener){
		mCompassListener = listener;
	}
	
	/**
	 * Sets the destination for the compass
	 * @author ricky barrette
	 */
	public void setDestination(GeoPoint destination){
		if(mCompass != null)
			mCompass.setDestination(destination);
	}
		
	/**
	 * UnResgisters the listener. after this call you will no longer get location updates
	 * @author Ricky Barrette
	 */
	public void unRegisterListener(){
		mListener = null;
	}
}