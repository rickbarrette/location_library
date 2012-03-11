/**
 * CompasOverlay.java
 * @date Mar 9, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.overlays;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.TypedValue;

import com.TwentyCodes.android.location.CompassListener;
import com.TwentyCodes.android.location.CompassSensor;
import com.TwentyCodes.android.location.GeoUtils;
import com.TwentyCodes.android.location.R;
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
	private int mNeedleResId = R.drawable.needle_sm;
	private int mBackgroundResId = R.drawable.compass_sm;
	private int mX;
	private int mY;
	private CompassListener mListener;

	/**
	 * Creates a new CompasOverlay
	 * @author ricky barrette
	 */
	public CompasOverlay(Context context) {
		mContext = context;
		mCompassSensor = new CompassSensor(context);
		mX = convertDipToPx(40);
		mY = mX;
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
	 * @param destination
	 * @param needleResId
	 * @param backgroundResId
	 * @param x dip
	 * @param y dip
	 * @author ricky barrette
	 */
	public CompasOverlay(Context context, GeoPoint destination, int needleResId, int backgroundResId, int x, int y){
		this(context, destination);
		mX = convertDipToPx(x);
		mY = convertDipToPx(y);
		mNeedleResId = needleResId;
		mBackgroundResId = backgroundResId;
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
	 * Converts dip to px
	 * @param dip
	 * @return px
	 * @author ricky barrette
	 */
	private int convertDipToPx(int i) {
		Resources r = mContext.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics());
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
	        matrix.postRotate(GeoUtils.calculateBearing(mLocation, mDestination, mBearing));
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
	 * @return return the current destination
	 * @author ricky barrette
	 */
	public GeoPoint getDestination(){
		return mDestination;
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
	 * @param x dip
	 * @param y dip
	 * @author ricky barrette
	 */
	public void setDrawables(int needleResId, int backgroundResId, int x, int y){
		mX = convertDipToPx(x);
		mY = convertDipToPx(y);
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