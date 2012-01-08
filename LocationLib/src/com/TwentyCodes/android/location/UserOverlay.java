/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Dec 28, 2010
 */
package com.TwentyCodes.android.location;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.TwentyCodes.android.SkyHook.R;
import com.TwentyCodes.android.debug.Debug;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * This is the standard version of the UserOverlay. 
 * @author ricky barrette
 */
public class UserOverlay extends Overlay implements GeoPointLocationListener, CompassListener {
	
	private boolean isEnabled;
	private int mUserArrow = R.drawable.user_arrow_animation_1;
	private Thread mAnimationThread;

	private float mBearing = 0;
	private int mAccuracy;
	private GeoPoint mPoint;
	private Context mContext;
	private AndroidGPS mAndroidGPS;
	private MapView mMapView;
	private ProgressDialog mGPSprogress;
	private boolean isFistFix = true;
	private GeoPointLocationListener mListener;
	public boolean isFollowingUser = true;
	private final String TAG = "SkyHookUserOverlay";
	private CompasOverlay mCompass;
	private boolean isCompassEnabled;
	private boolean isGPSDialogEnabled;
	
	/**
	 * Construct a new SkyHookUserOverlaymFollowUser
	 * @param mapView
	 * @param context
	 * @author ricky barrette
	 */
	public UserOverlay(MapView mapView, Context context) {
		mContext = context;
		mMapView = mapView;
		mAndroidGPS = new AndroidGPS(context);
		mCompass = new CompasOverlay(context);
		mUserArrow = R.drawable.user_arrow_animation_1;
	}
	
	/**
	 * Construct a new SkyHookUserOverlay
	 * @param mapView
	 * @param context
	 * @param followUser
	 * @author ricky barrette
	 */
	public UserOverlay(MapView mapView, Context context, boolean followUser) {
		this(mapView, context);
		isFollowingUser = followUser;
	}
	
	/**
	 * Disables the compass
	 * @author ricky barrette
	 */
	public void disableCompass(){
		isCompassEnabled = false;
		mMapView.getOverlays().remove(mCompass);
		
	}
	
	/**
	 * Stops location updates and removes the overlay from view
	 * @author ricky barrette
	 */
	public void disableMyLocation(){
		Log.d(TAG,"disableMyLocation()");
		mAndroidGPS.disableLocationUpdates();
		isEnabled = false;
		mCompass.disable();
		if(mGPSprogress != null)
			mGPSprogress.cancel();
	}
	
	/**
	 * Disables the Acquiring GPS dialog
	 * @author ricky barrette
	 */
	public void disableGPSDialog(){
		isGPSDialogEnabled = false;
		if(mGPSprogress != null)
			mGPSprogress.dismiss();
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
//			Log.d(SkyHook.TAG, (GeoUtils.distanceKm(mPoint, leftGeo) * 1000)+"m");
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
//        paint.setColor(Color.RED);
//		RectF oval = new RectF(left.x - 1, left.y - 1, left.x + 1, left.y + 1);
//		canvas.drawOval(oval, paint);
    	
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
    public synchronized void enableCompass(){
    	if(! this.isCompassEnabled){
    		this.mMapView.getOverlays().add(this.mCompass);
    		this.isCompassEnabled = true;
    	}
    }
    
    /**
     * Enables the Acquiring GPS dialog if the location has not been acquired
     * @author ricky barrette
     */
    public void enableGPSDialog(){
    	isGPSDialogEnabled = true;
    	if(isFistFix)
    		if(mGPSprogress != null){
    			if(! mGPSprogress.isShowing())
    				mGPSprogress = ProgressDialog.show(mContext, "", mContext.getText(R.string.gps_fix), true, true);
    		} else
    			mGPSprogress = ProgressDialog.show(mContext, "", mContext.getText(R.string.gps_fix), true, true);
    }
    
    /**
     * Attempts to enable MyLocation, registering for updates from sky hook
     * @author ricky barrette
     */
    public void enableMyLocation(){
    	Log.d(TAG,"enableMyLocation()");
    	if (! isEnabled) {
    		
    		mAnimationThread = new AnimationThread();
    		mAnimationThread.start();
    		
			mAndroidGPS.enableLocationUpdates(this);
			isEnabled = true;
			mCompass.enable(this);
			isFistFix = true;
			if(isGPSDialogEnabled)
				enableGPSDialog();
			
			/**
			 * this is a message that tells the user that we are having trouble getting an GPS signal
			 */
			new Handler().postAtTime(new Runnable() {
				@Override
				public void run() {
					if (mGPSprogress.isShowing()) {
						mGPSprogress.cancel();
						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						builder.setMessage(
								mContext.getText(R.string.sorry_theres_trouble))
								.setCancelable(false)
								.setPositiveButton(mContext.getText(android.R.string.ok),
										new DialogInterface.OnClickListener() {
											public void onClick( DialogInterface dialog, int id) {
												dialog.cancel();
											}
										});
						builder.show();
					}
				}
			}, SystemClock.uptimeMillis()+90000L);
		}
    }
    
    /**
     * Allows the map to follow the user
     * @param followUser
     * @author ricky barrette
     */
    public void followUser(boolean followUser){
    	Log.d(TAG,"followUser()");
    	isFollowingUser = followUser;
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
		if(Debug.DEBUG)
			Log.v(TAG, "onCompassUpdate()");
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
			if(mGPSprogress != null)
				mGPSprogress.dismiss();
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
		Log.d(TAG,"unRegisterListener()");
		mListener = null;
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
	 * This thread is responsible for animating the user icon
	 * @author ricky barrette
	 */
	private class AnimationThread extends Thread {

		/**
		 * Main method of this animation thread
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override 
		public void run(){
			super.run();
			int index = 0;
			boolean isCountingUp = true;
			while (true) {
				synchronized (this) {
					if (! isEnabled) {
						break;
					}
					
					switch(index){
					case 1:
						setDrawable(R.drawable.user_arrow_animation_2);
						break;
					case 2:
						setDrawable(R.drawable.user_arrow_animation_3);
						break;
					default:
						setDrawable(R.drawable.user_arrow_animation_1);
						try {
							sleep(300l);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
						break;
					}
					
					try {
						sleep(200l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(isCountingUp){
						if(index++ == 2)
							isCountingUp = false;
					} else if(index-- == 0)
						isCountingUp = true;
					
				}
			}
			
		}
		
		/**
		 * Sets the id of the user drawable
		 * @param id
		 * @author ricky barrette
		 */
		private synchronized void setDrawable(int id){
			mUserArrow = id;
		}
	}
}