/**
 * UserOverlayMapFragment.java
 * @date Jan 12, 2012
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
package com.TwentyCodes.android.fragments;

import com.TwentyCodes.android.location.CompassSensor.CompassListener;
import com.TwentyCodes.android.location.GeoPointLocationListener;
import com.TwentyCodes.android.location.MapView;
import com.TwentyCodes.android.overlays.UserOverlay;
import com.google.android.maps.GeoPoint;

/**
 * This is a MapFragment that maintains the UserOverlay
 * 
 * TODO acquiring gps dialog
 * 
 * @author ricky barrette
 */
public class UserOverlayMapFragment extends BaseMapFragment implements GeoPointLocationListener, CompassListener {

	private UserOverlay mUserOverlay;
	private GeoPointLocationListener mGeoPointLocationListener;
	private CompassListener mCompassListener;

	/**
	 * Creates a new UserOverlayMapFragment
	 * 
	 * @author ricky barrette
	 */
	public UserOverlayMapFragment() {
		super();
	}

	/**
	 * Tells the useroverlay to pan the map to follow the user
	 * 
	 * @param followUser
	 * @author ricky barrette
	 */
	public void followUser(final boolean followUser) {
		mUserOverlay.followUser(followUser);
	}

	/**
	 * @return return the current destination
	 * @author ricky barrette
	 */
	public GeoPoint getDestination() {
		return mUserOverlay.getDestination();
	}

	/**
	 * @return the users current location
	 * @author ricky barrette
	 */
	public GeoPoint getUserLocation() {
		return mUserOverlay.getUserLocation();
	}

	/**
	 * Called when the compass is updated (non-Javadoc)
	 * 
	 * @see com.TwentyCodes.android.location.CompassListener#onCompassUpdate(float)
	 */
	@Override
	public void onCompassUpdate(final float bearing) {
		if (mCompassListener != null)
			mCompassListener.onCompassUpdate(bearing);
	}

	@Override
	public void onFirstFix(final boolean isFistFix) {
		if (mGeoPointLocationListener != null)
			mGeoPointLocationListener.onFirstFix(isFistFix);
	}

	/**
	 * Called when skyhook has a location to report
	 * 
	 * @author ricky barrette
	 */
	@Override
	public void onLocationChanged(final GeoPoint point, final int accuracy) {
		if (mGeoPointLocationListener != null)
			mGeoPointLocationListener.onLocationChanged(point, accuracy);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.TwentyCodes.android.fragments.BaseMapFragment#onMapViewCreate(com.TwentyCodes.android.location.MapView)
	 */
	@Override
	public void onMapViewCreate(final MapView map) {
		mUserOverlay = new UserOverlay(map, getActivity().getApplicationContext());
		mUserOverlay.registerListener(this);
		mUserOverlay.setCompassListener(this);
		mUserOverlay.enableCompass();
		mUserOverlay.followUser(true);

		map.getOverlays().add(mUserOverlay);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.TwentyCodes.android.fragments.BaseMapFragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		mUserOverlay.disableMyLocation();
		removeOverlay(mUserOverlay);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.TwentyCodes.android.fragments.BaseMapFragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (mUserOverlay != null) {
			mUserOverlay.enableMyLocation();
			addOverlay(mUserOverlay);
		}
	}

	/**
	 * reorders the overlays to the UserOverlay always on top
	 * 
	 * @author ricky barrette
	 */
	public void reorderOverlays() {
		getMap().getOverlays().remove(mUserOverlay);
		getMap().getOverlays().add(mUserOverlay);
	}

	/**
	 * @param needleResId
	 * @param backgroundResId
	 * @param x
	 * @param y
	 * @author ricky barrette
	 */
	public void setCompassDrawables(final int needleResId, final int backgroundResId, final int x, final int y) {
		mUserOverlay.setCompassDrawables(needleResId, backgroundResId, x, y);
	}

	/**
	 * @param listener
	 * @author ricky barrette
	 */
	public void setCompassListener(final CompassListener listener) {
		mCompassListener = listener;
	}

	/**
	 * Sets the destination for the compass to point to
	 * 
	 * @param destination
	 * @author ricky barrette
	 */
	public void setDestination(final GeoPoint destination) {
		mUserOverlay.setDestination(destination);
	}

	/**
	 * @param listener
	 * @author ricky barrette
	 */
	public void setGeoPointLocationListener(final GeoPointLocationListener listener) {
		mGeoPointLocationListener = listener;
	}
}