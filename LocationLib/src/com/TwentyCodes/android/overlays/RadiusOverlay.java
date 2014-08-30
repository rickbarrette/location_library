/**
 * @author Twenty Codes
 * @author ricky barrette
 */

package com.TwentyCodes.android.overlays;

import com.TwentyCodes.android.location.OnLocationSelectedListener;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * This class will used to draw a radius of a specified size in a specified
 * location, then inserted into an overlay list to be displayed a map
 * 
 * @author ricky barrette
 */
public class RadiusOverlay {

	private final CircleOptions mCircleOptions;
	private OnLocationSelectedListener mListener;

	/**
	 * Creates a new RadiusOverlay
	 * 
	 * @author ricky barrette
	 */
	public RadiusOverlay() {
		mCircleOptions = new CircleOptions();
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
	public RadiusOverlay(final LatLng point, final double radius, final int color) {
		mCircleOptions = new CircleOptions();
		mCircleOptions.center(point);
		mCircleOptions.radius(radius);
		mCircleOptions.fillColor(color);
	}

	/**
	 * @return the selected location
	 * @author ricky barrette
	 */
	public LatLng getLocation() {
		return mCircleOptions.getCenter();
	}

	public int getZoomLevel() {
		// GeoUtils.GeoUtils.distanceFrom(mPoint , mRadius)
		return 0;
	}
//
//	@Override
//	public boolean onTap(final GeoPoint p, final MapView mapView) {
//		mPoint = p;
//		if (mListener != null)
//			mListener.onLocationSelected(p);
//		return super.onTap(p, mapView);
//	}

	/**
	 * @param color
	 * @author ricky barrette
	 */
	public void setColor(final int color) {
		mCircleOptions.fillColor(color);
	}

	/**
	 * @param location
	 * @author ricky barrette
	 */
	public void setLocation(final LatLng location) {
		mCircleOptions.center(location);
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
	public void setRadius(final double radius) {
		mCircleOptions.radius(radius);
	}

	/**
	 *
	 * @return
	 */
	public CircleOptions getCircleOptions(){
		return mCircleOptions;
	}
}