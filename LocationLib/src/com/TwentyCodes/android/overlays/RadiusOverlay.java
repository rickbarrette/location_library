/**
 * @author Twenty Codes
 * @author ricky barrette
 */

package com.TwentyCodes.android.overlays;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;

import com.TwentyCodes.android.location.GeoUtils;
import com.TwentyCodes.android.location.OnLocationSelectedListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

/**
 * This class will used to draw a radius of a specified size in a specified
 * location, then inserted into an overlay list to be displayed a map
 * 
 * @author ricky barrette
 */
public class RadiusOverlay extends Overlay {

	public OverlayItem mOverlayItem;
	private GeoPoint mPoint;
	private float mRadius = 0;
	private int mColor = Color.GREEN;
	private GeoPoint mRadiusPoint;
	private OnLocationSelectedListener mListener;

	/**
	 * Creates a new RadiusOverlay
	 * 
	 * @author ricky barrette
	 */
	public RadiusOverlay() {
	}

	/**
	 * Creates a new RadiusOverlay object that can be inserted into an overlay
	 * list.
	 * 
	 * @param point
	 *            center of radius geopoint
	 * @param radius
	 *            radius in meters
	 * @param color
	 *            desired color of the radius from Color API
	 * @author ricky barrette
	 */
	public RadiusOverlay(final GeoPoint point, final float radius, final int color) {
		mPoint = point;
		mRadius = radius;
		mColor = color;
	}

	/**
	 * draws a specific radius on the mapview that is handed to it
	 * 
	 * @param canvas
	 *            canvas to be drawn on
	 * @param mapView
	 * @param shadow
	 * @param when
	 */
	@Override
	public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
		if (mPoint != null) {
			final Paint paint = new Paint();
			final Point center = new Point();
			final Point left = new Point();
			final Projection projection = mapView.getProjection();

			/*
			 * Calculate a geopoint that is "radius" meters away from geopoint
			 * point and convert the given GeoPoint and leftGeo to onscreen
			 * pixel coordinates, relative to the top-left of the MapView that
			 * provided this Projection.
			 */
			mRadiusPoint = GeoUtils.distanceFrom(mPoint, mRadius);
			projection.toPixels(mRadiusPoint, left);
			projection.toPixels(mPoint, center);

			/*
			 * get radius of the circle being drawn by
			 */
			int circleRadius = center.x - left.x;
			if (circleRadius <= 0)
				circleRadius = left.x - center.x;

			/*
			 * paint a circle on the map
			 */
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2.0f);
			paint.setColor(mColor);
			paint.setStyle(Style.STROKE);
			canvas.drawCircle(center.x, center.y, circleRadius, paint);

			// draw a dot over the geopoint
			final RectF oval = new RectF(center.x - 2, center.y - 2, center.x + 2, center.y + 2);
			canvas.drawOval(oval, paint);

			// fill the radius with a nice green
			paint.setAlpha(25);
			paint.setStyle(Style.FILL);
			canvas.drawCircle(center.x, center.y, circleRadius, paint);
		}
	}

	/**
	 * @return the selected location
	 * @author ricky barrette
	 */
	public GeoPoint getLocation() {
		return mPoint;
	}

	public int getZoomLevel() {
		// GeoUtils.GeoUtils.distanceFrom(mPoint , mRadius)
		return 0;
	}

	@Override
	public boolean onTap(final GeoPoint p, final MapView mapView) {
		mPoint = p;
		if (mListener != null)
			mListener.onLocationSelected(p);
		return super.onTap(p, mapView);
	}

	/**
	 * @param color
	 * @author ricky barrette
	 */
	public void setColor(final int color) {
		mColor = color;
	}

	/**
	 * @param location
	 * @author ricky barrette
	 */
	public void setLocation(final GeoPoint location) {
		mPoint = location;
	}

	public void setLocationSelectedListener(final OnLocationSelectedListener listener) {
		mListener = listener;
	}

	/**
	 * @param radius
	 *            in meters
	 * @author ricky barrette
	 * @param radius
	 */
	public void setRadius(final int radius) {
		mRadius = radius;
	}
}