/**
 * CompasOverlay.java
 * @date Mar 9, 2011
 * @author ricky barrette
 * 
 * Copyright 2012 Richard Barrette 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License
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

import com.TwentyCodes.android.location.CompassSensor;
import com.TwentyCodes.android.location.CompassSensor.CompassListener;
import com.TwentyCodes.android.location.GeoUtils;
import com.TwentyCodes.android.location.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * A Simple compass overlay that will be used to point towards a destination or
 * north
 * 
 * @author ricky barrette
 */
public class CompasOverlay extends Overlay implements CompassListener {

	private float mBearing;
	private final Context mContext;
	private GeoPoint mDestination;
	private GeoPoint mLocation;
	private boolean isEnabled;
	private final CompassSensor mCompassSensor;
	private int mNeedleResId = R.drawable.needle_sm;
	private int mBackgroundResId = R.drawable.compass_sm;
	private int mX;
	private int mY;
	private CompassListener mListener;

	/**
	 * Creates a new CompasOverlay
	 * 
	 * @author ricky barrette
	 */
	public CompasOverlay(final Context context) {
		mContext = context;
		mCompassSensor = new CompassSensor(context);
		mX = convertDipToPx(40);
		mY = mX;
	}

	/**
	 * Creates a new CompasOverlay
	 * 
	 * @param context
	 * @param destination
	 * @author ricky barrette
	 */
	public CompasOverlay(final Context context, final GeoPoint destination) {
		this(context);
		mDestination = destination;
	}

	/**
	 * Creates a new CompasOverlay
	 * 
	 * @param context
	 * @param destination
	 * @param needleResId
	 * @param backgroundResId
	 * @param x
	 *            dip
	 * @param y
	 *            dip
	 * @author ricky barrette
	 */
	public CompasOverlay(final Context context, final GeoPoint destination, final int needleResId, final int backgroundResId, final int x, final int y) {
		this(context, destination);
		mX = convertDipToPx(x);
		mY = convertDipToPx(y);
		mNeedleResId = needleResId;
		mBackgroundResId = backgroundResId;
	}

	/**
	 * Creates a new CompasOverlay
	 * 
	 * @param context
	 * @param needleResId
	 * @param backgroundResId
	 * @param x
	 * @param y
	 * @author ricky barrette
	 */
	public CompasOverlay(final Context context, final int needleResId, final int backgroundResId, final int x, final int y) {
		this(context, null, needleResId, backgroundResId, x, y);
	}

	/**
	 * Converts dip to px
	 * 
	 * @param dip
	 * @return px
	 * @author ricky barrette
	 */
	private int convertDipToPx(final int i) {
		final Resources r = mContext.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics());
	}

	/**
	 * Disables the compass overlay
	 * 
	 * @author ricky barrette
	 */
	public void disable() {
		isEnabled = false;
		mCompassSensor.disable();
		mListener = null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas,
	 *      com.google.android.maps.MapView, boolean)
	 * @author ricky barrette
	 */
	@Override
	public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {

		if (isEnabled) {
			// set the center of the compass in the top left corner of the
			// screen
			final Point point = new Point();
			point.set(mX, mY);

			// draw compass background
			final Bitmap compass = BitmapFactory.decodeResource(mContext.getResources(), mBackgroundResId);
			canvas.drawBitmap(compass, point.x - compass.getWidth() / 2, point.y - compass.getHeight() / 2, null);

			// draw the compass needle
			final Bitmap arrowBitmap = BitmapFactory.decodeResource(mContext.getResources(), mNeedleResId);
			final Matrix matrix = new Matrix();
			matrix.postRotate(GeoUtils.calculateBearing(mLocation, mDestination, mBearing));
			final Bitmap rotatedBmp = Bitmap.createBitmap(arrowBitmap, 0, 0, arrowBitmap.getWidth(), arrowBitmap.getHeight(), matrix, true);
			canvas.drawBitmap(rotatedBmp, point.x - rotatedBmp.getWidth() / 2, point.y - rotatedBmp.getHeight() / 2, null);
			mapView.invalidate();
		}
		super.draw(canvas, mapView, shadow);
	}

	/**
	 * Enables the compass overlay
	 * 
	 * @author ricky barrette
	 */
	public void enable() {
		if (!isEnabled) {
			isEnabled = true;
			mCompassSensor.enable(this);
		}
	}

	/**
	 * Enables the compass overlay
	 * 
	 * @param listener
	 * @author ricky barrette
	 */
	public void enable(final CompassListener listener) {
		mListener = listener;
		enable();
	}

	/**
	 * @return the current bearing
	 * @author ricky barrette
	 */
	public float getBearing() {
		return mBearing;
	}

	/**
	 * @return return the current destination
	 * @author ricky barrette
	 */
	public GeoPoint getDestination() {
		return mDestination;
	}

	/**
	 * Called from the compass Sensor to update the current bearing
	 * (non-Javadoc)
	 * 
	 * @see com.TwentyCodes.android.location.CompassListener#onCompassUpdate(float)
	 * @author ricky barrette
	 */
	@Override
	public void onCompassUpdate(final float bearing) {
		mBearing = bearing;

		/*
		 * pass it down the chain
		 */
		if (mListener != null)
			mListener.onCompassUpdate(bearing);
	}

	/**
	 * @param destination
	 * @author ricky barrette
	 */
	public void setDestination(final GeoPoint destination) {
		mDestination = destination;
	}

	/**
	 * @param needleResId
	 * @param backgroundResId
	 * @param x
	 *            dip
	 * @param y
	 *            dip
	 * @author ricky barrette
	 */
	public void setDrawables(final int needleResId, final int backgroundResId, final int x, final int y) {
		mX = convertDipToPx(x);
		mY = convertDipToPx(y);
		mNeedleResId = needleResId;
		mBackgroundResId = backgroundResId;
	}

	/**
	 * @param location
	 * @author ricky barrette
	 */
	public void setLocation(final GeoPoint location) {
		mLocation = location;
	}

}