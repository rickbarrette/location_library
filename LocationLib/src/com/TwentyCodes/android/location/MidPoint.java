/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Nov 30, 2010
 */
package com.TwentyCodes.android.location;

import com.google.android.maps.GeoPoint;

/**
 * This MidPoint object will hold the information form the calculations performed by GeoUtils.midPoint(). 
 * @author ricky barrette
 */
public class MidPoint {
	
	private final int mMinLatitude;
	private final int mMaxLatitude;
	private final int mMinLongitude;
	private final int mMaxLongitude;
	private final GeoPoint mMidPoint;

	/**
	 * Creates a new MidPoint
	 * @author ricky barrette
	 */
	public MidPoint(GeoPoint midPoint, int minLatitude, int minLongitude, int maxLatitude, int maxLongitude) {
		mMinLatitude = minLatitude;
		mMaxLatitude = maxLatitude;
		mMinLongitude = minLongitude;
		mMaxLongitude = maxLongitude;
		mMidPoint = midPoint;
	}
	
	/**
	 * zooms the provided map view to the span of this mid point
	 * @param mMapView
	 * @author ricky barrette
	 */
	public void zoomToSpan(com.google.android.maps.MapView mMapView){
		mMapView.getController().zoomToSpan((mMaxLatitude - mMinLatitude), (mMaxLongitude - mMinLongitude));
	}
	
	/**
	 * returns the calculated midpoint
	 * @return
	 * @author ricky barrette
	 */
	public GeoPoint getMidPoint(){
		return mMidPoint;
	}
}