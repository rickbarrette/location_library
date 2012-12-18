/**
 * MidPoint.java
 * @author ricky barrette
 * @date Nov 30, 2010
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
package com.TwentyCodes.android.location;

import com.google.android.maps.GeoPoint;

/**
 * This MidPoint object will hold the information form the calculations
 * performed by GeoUtils.midPoint().
 * 
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
	 * 
	 * @author ricky barrette
	 */
	public MidPoint(final GeoPoint midPoint, final int minLatitude, final int minLongitude, final int maxLatitude, final int maxLongitude) {
		mMinLatitude = minLatitude;
		mMaxLatitude = maxLatitude;
		mMinLongitude = minLongitude;
		mMaxLongitude = maxLongitude;
		mMidPoint = midPoint;
	}

	/**
	 * returns the calculated midpoint
	 * 
	 * @return
	 * @author ricky barrette
	 */
	public GeoPoint getMidPoint() {
		return mMidPoint;
	}

	/**
	 * zooms the provided map view to the span of this mid point
	 * 
	 * @param mMapView
	 * @author ricky barrette
	 */
	public void zoomToSpan(final com.google.android.maps.MapView mMapView) {
		mMapView.getController().zoomToSpan(mMaxLatitude - mMinLatitude, mMaxLongitude - mMinLongitude);
	}
}