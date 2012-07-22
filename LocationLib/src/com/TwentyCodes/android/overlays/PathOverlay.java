/**
 * PathOverlay.java
 * @date Nov 11, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.overlays;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * This imutable overlay class is used to draw a path and points on a map
 * 
 * @author ricky barrette
 */
public final class PathOverlay extends Overlay {

	private static final int PATH = 0;
	private static final int POINT = 1;
	private final GeoPoint mStart;
	private final GeoPoint mEnd;
	private final int mColor;
	private final int mMode;
	private final int mRadius;

	/**
	 * Creates a new PathOverlay in path mode
	 * 
	 * @author ricky barrette
	 */
	public PathOverlay(final GeoPoint start, final GeoPoint end, final int color) {
		mStart = start;
		mEnd = end;
		mColor = color;
		mMode = PATH;
		mRadius = 0;
	}

	/**
	 * Creates a new PathOverlay in point mode. This is used to draw end points.
	 * 
	 * @param point
	 * @param radius
	 * @param color
	 * @author ricky barrette
	 */
	public PathOverlay(final GeoPoint point, final int radius, final int color) {
		mMode = POINT;
		mRadius = radius;
		mStart = point;
		mEnd = mStart;
		mColor = color;
	}

	/**
	 * 
	 * @param canvas
	 *            canvas to be drawn on
	 * @param mapView
	 * @param shadow
	 * @param when
	 */
	@Override
	public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
		final Projection projection = mapView.getProjection();
		final Paint paint = new Paint();
		paint.setColor(mColor);
		paint.setAntiAlias(true);
		final Point point = new Point();
		projection.toPixels(mStart, point);

		switch (mMode) {
		case POINT:
			final RectF oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y + mRadius);
			canvas.drawOval(oval, paint);
		case PATH:
			final Point point2 = new Point();
			projection.toPixels(mEnd, point2);
			paint.setStrokeWidth(5);
			paint.setAlpha(120);
			canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
		}
		super.draw(canvas, mapView, shadow);
	}

	/**
	 * @return the end point of this path
	 * @author ricky barrette
	 */
	public GeoPoint getEndPoint() {
		return mEnd;
	}

	/**
	 * @return the start point of this path
	 * @author ricky barrette
	 */
	public GeoPoint getStartPoint() {
		return mStart;
	}
}