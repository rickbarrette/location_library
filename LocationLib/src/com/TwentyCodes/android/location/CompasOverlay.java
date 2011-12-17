/**
 * CompasOverlay.java
 * @date Mar 9, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

import com.TwentyCodes.android.SkyHook.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * A Simple compass overlay that will be used to point towards a destination or north
 * @author ricky barrette
 */
public class CompasOverlay extends Overlay implements CompassListener {

	private float mBearing;
	private Context mContext;
	private GeoPoint mDestination;
	private GeoPoint mLocation;
	private boolean isEnabled;
	private CompassSensor mCompassSensor;
	private int mNeedleResId = R.drawable.needle;
	private int mBackgroundResId = R.drawable.compass;
	private int mX = 100;
	private int mY = 100;
	private CompassListener mListener;

	/**
	 * Creates a new CompasOverlay
	 * @author ricky barrette
	 */
	public CompasOverlay(Context context) {
		mContext = context;
		mCompassSensor = new CompassSensor(context);
	}
	
	/**
	 * Creates a new CompasOverlay
	 * @param context
	 * @param destination
	 * @author ricky barrette
	 */
	public CompasOverlay(Context context, GeoPoint destination){
		this(context);
		mDestination = destination;
	}
	
	/**
	 * Creates a new CompasOverlay
	 * @param context
	 * @param needleResId
	 * @param backgroundResId
	 * @param x
	 * @param y
	 * @author ricky barrette
	 */
	public CompasOverlay(Context context, int needleResId, int backgroundResId, int x, int y){
		this(context, null, needleResId, backgroundResId, x, y);
	}

	/**
	 * Creates a new CompasOverlay
	 * @param context
	 * @param destination
	 * @param needleResId
	 * @param backgroundResId
	 * @param x
	 * @param y
	 * @author ricky barrette
	 */
	public CompasOverlay(Context context, GeoPoint destination, int needleResId, int backgroundResId, int x, int y){
		this(context, destination);
		mX = x;
		mY = y;
		mNeedleResId = needleResId;
		mBackgroundResId = backgroundResId;
	}
	
	/**
	 * Calculated the bearing from the current location to the current destination, or returns the bearing for north if there is no destination
	 * @return bearing
	 * @author ricky barrette
	 */
	private float calculateBearing() {
		if( (mLocation == null) || (mDestination == null) )
			return mBearing;
		
		float bearing = mBearing - GeoUtils.bearing(mLocation, mDestination).floatValue();
		if (bearing != 0)
			bearing = 360 - bearing;
		
		return bearing;
	}

	/**
	 * Disables the compass overlay
	 * @author ricky barrette
	 */
	public void disable(){
		isEnabled = false;
		mCompassSensor.disable();
		mListener = null;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 * @author ricky barrette
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		
		if(isEnabled){
			//set the center of the compass in the top left corner of the screen
			Point point = new Point();
			point.set(mX, mY);
			
			//draw compass background
			Bitmap compass = BitmapFactory.decodeResource( mContext.getResources(), mBackgroundResId);
			canvas.drawBitmap(compass, 
		            point.x - (compass.getWidth()  / 2), 
		            point.y - (compass.getHeight() / 2), 
		            null
		        );
			
			//draw the compass needle
	        Bitmap arrowBitmap = BitmapFactory.decodeResource( mContext.getResources(), mNeedleResId);
	        Matrix matrix = new Matrix();
	        matrix.postRotate(calculateBearing());
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
			mapView.invalidate();
		}
	    super.draw(canvas, mapView, shadow);
	}
	
	/**
	 * Enables the compass overlay
	 * @author ricky barrette
	 */
	public void enable(){
		if(! isEnabled){
			isEnabled = true;
			mCompassSensor.enable(this);
		}
	}
	
	/**
	 * Enables the compass overlay
	 * @param listener
	 * @author ricky barrette
	 */
	public void enable(CompassListener listener){
		mListener = listener;
		enable();
	}
	
	/**
	 * @return the current bearing
	 * @author ricky barrette
	 */
	public float getBearing(){
		return mBearing;
	}

	/**
	 * Called from the compass Sensor to update the current bearing
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.location.CompassListener#onCompassUpdate(float)
	 * @author ricky barrette
	 */
	@Override
	public void onCompassUpdate(float bearing) {
		mBearing = bearing;
		
		/*
		 * pass it down the chain
		 */
		if(mListener != null)
			mListener.onCompassUpdate(bearing);
	}
	
	/**
	 * @param destination
	 * @author ricky barrette
	 */
	public void setDestination(GeoPoint destination){
		mDestination = destination;
	}
	
	/**
	 * @param needleResId
	 * @param backgroundResId
	 * @author ricky barrette
	 */
	public void setDrawables(int needleResId, int backgroundResId, int x, int y){
		mX = x;
		mY = y;
		mNeedleResId = needleResId;
		mBackgroundResId = backgroundResId;
	}
	
	/**
	 * @param location
	 * @author ricky barrette
	 */
	public void setLocation(GeoPoint location){
		mLocation = location;
	}

}