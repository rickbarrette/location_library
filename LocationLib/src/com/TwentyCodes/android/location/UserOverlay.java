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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;

import com.TwentyCodes.android.SkyHook.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

/**
 * This is the standard version of the UserOverlay. 
 * @author ricky barrette
 */
public class UserOverlay extends MyLocationOverlay {
	
	private Context mContext;
	private MapView mMapView;
	private ProgressDialog mGPSprogress;
	private boolean isFirstFix = true;
	private GeoPointLocationListener mListener;
	private boolean isFollowingUser = true;
	private float myAzimuth;
	private GeoPoint mUser;
	private GeoPoint mDest;
	private boolean isShowingCompass;
	private AnimationDrawable mUserArrow;

	/**
	 * Creates a new UserOverlay
	 * @param context
	 * @param mapView
	 * @author ricky barrette
	 */
	public UserOverlay(Context context, MapView mapView) {
		super(context, mapView);
		mMapView = mapView;
		mContext = context;
		mUserArrow = (AnimationDrawable) mContext.getResources().getAnimation(R.drawable.userarrow);
    	mUserArrow.start();
	}
	
	/**
	 * disables the compass view
	 * (non-Javadoc)
	 * @see com.google.android.maps.MyLocationOverlay#disableCompass()
	 * @author ricky barrette
	 */
	@Override
	public void disableCompass(){
		isShowingCompass = false;
	}
	
	/**
	 * called when the overlay is disabled. this will disable all progress dialogs, and location based servicess
	 * (non-Javadoc)
	 * @see com.google.android.maps.MyLocationOverlay#disableMyLocation()
	 * @author ricky barrette
	 */
	@Override
	public void disableMyLocation(){
		super.disableCompass();
		super.disableMyLocation();
		mGPSprogress.dismiss();
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
	 * computes bearing to geopoint based on device oriantaion and draws the compass of what you want really really badly on screen
	 * @param - canvas - the canvas to draw on
	 * @param - bearing - bearing of user based on magnetic compass
	 * @author ricky barrette
	 */
	@Override
	protected void drawCompass(Canvas canvas, float bearing){
		myAzimuth = bearing;

		mMapView.invalidate();

		if (isShowingCompass) {
			/*
			 * if the dest and user geopoint are not null, then draw the compass point to the dest geopoint
			 * 
			 * else draw the compass to point north
			 */
			if (mUser != null && mDest != null){
				Double d = GeoUtils.bearing(mUser, mDest);
				bearing = bearing - d.floatValue();
			} else if (bearing != 0){
				bearing = 360 - bearing;
			}
			
			super.drawCompass(canvas, bearing);
		}
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
	protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint point, long when){
		if (point != null) {

			Point center = new Point();
			Point left = new Point();
			Projection projection = mapView.getProjection();
			GeoPoint leftGeo = GeoUtils.distanceFrom(point, lastFix.getAccuracy());
			projection.toPixels(leftGeo, left);
			projection.toPixels(point, center);
			canvas = drawAccuracyCircle(center, left, canvas);
			canvas = drawUser(center, myAzimuth, canvas);
			/*
			 * the following log is used to demonstrate if the leftGeo point is the correct
			 */
//			Log.d(SkyHook.TAG, (GeoUtils.distanceKm(mPoint, leftGeo) * 1000)+"m");
		}
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
        Bitmap arrowBitmap = ((BitmapDrawable)mUserArrow.getCurrent()).getBitmap();
        Matrix matrix = new Matrix();
        matrix.postRotate(bearing);
        Bitmap rotatedBmp = Bitmap.createBitmap(
            arrowBitmap, 
            0, 0, 
            arrowBitmap.getWidth(), 
            arrowBitmap.getHeight(), 
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
	 * enables the compass view
	 * (non-Javadoc)
	 * @see com.google.android.maps.MyLocationOverlay#enableCompass()
	 * @author ricky barrette
	 */
	@Override
	public boolean enableCompass(){
		isShowingCompass = true;
		return isShowingCompass;
	}
	
    /**
	 * called when the user overlay is enabled, this will display the progress dialog
	 * (non-Javadoc)
	 * @see com.google.android.maps.MyLocationOverlay#enableMyLocation()
	 * @author ricky barrette
	 */
	@Override
	public boolean enableMyLocation(){
		mGPSprogress = ProgressDialog.show(mContext, "", mContext.getText(R.string.gps_fix), true, true);
		isFirstFix = true;
		super.enableCompass();
		
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
		
		return super.enableMyLocation();
	}
    
    /**
     * Allows the map to follow the user
     * @param followUser
     * @author ricky barrette
     */
    public void followUser(boolean followUser){
    	isFollowingUser = followUser;
    }

	/**
	 * called when the SkyHook location changes, this method is resposiable for updating the overlay location and accuracy circle.
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.SkyHook.GeoPointLocationListener.location.LocationListener#onLocationChanged(com.google.android.maps.GeoPoint, float)
	 * @param point
	 * @param accuracy
	 * @author ricky barrette
	 */
	@Override
	public void onLocationChanged(Location location) {
		
		GeoPoint point = new GeoPoint((int) (location.getLatitude() *1e6), (int) (location.getLongitude() *1e6));
		
		/*
		 * if this is the first fix
		 * set map center the users location, and zoom to the max zoom level
		 */
		if(point != null && isFirstFix){
			mMapView.getController().setCenter(point);
			mMapView.getController().setZoom(mMapView.getMaxZoomLevel()+3);
			mGPSprogress.dismiss();
			isFirstFix = false;
		}
		
		//pan to user if off map
		if (isFollowingUser) {
			panToUserIfOffMap(point);
		}
		
		mListener.onLocationChanged(point, (int) location.getAccuracy());
		super.onLocationChanged(location);
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
		if (mListener == null){
			mListener = listener;
		}
	}
	
	/**
	 * UnResgisters the listener. after this call you will no longer get location updates
	 * @author Ricky Barrette
	 */
	public void unRegisterListener(){
		mListener = null;
	}

}