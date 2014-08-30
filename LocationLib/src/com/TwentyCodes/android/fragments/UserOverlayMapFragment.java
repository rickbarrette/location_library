/**
 * UserOverlayMapFragment.java
 * @date Jan 12, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.fragments;

import com.TwentyCodes.android.location.CompassSensor.CompassListener;
import com.TwentyCodes.android.location.LatLngListener;
import com.TwentyCodes.android.location.MapView;
import com.TwentyCodes.android.overlays.UserOverlay;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * This is a MapFragment that maintains the UserOverlay
 * 
 * TODO acquiring gps dialog
 * 
 * @author ricky barrette
 */
public class UserOverlayMapFragment extends MapFragment implements LatLngListener, CompassListener {

	private UserOverlay mUserOverlay;
	private LatLngListener mLatLngListener;
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
	public LatLng getDestination() {
		return mUserOverlay.getDestination();
	}

	/**
	 * @return the users current location
	 * @author ricky barrette
	 */
	public LatLng getUserLocation() {
		return mUserOverlay.getUserLocation();
	}

	/**
	 * Called when the compass is updated (non-Javadoc)
	 *
	 */
	@Override
	public void onCompassUpdate(final float bearing) {
		if (mCompassListener != null)
			mCompassListener.onCompassUpdate(bearing);
	}

	@Override
	public void onFirstFix(final boolean isFistFix) {
		if (mLatLngListener != null)
			mLatLngListener.onFirstFix(isFistFix);
	}

	/**
	 * Called when skyhook has a location to report
	 * 
	 * @author ricky barrette
	 */
	@Override
	public void onLocationChanged(final LatLng point, final int accuracy) {
		if (mLatLngListener != null)
			mLatLngListener.onLocationChanged(point, accuracy);
	}

	/**
	 * (non-Javadoc)
	 * 
	 */
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
	 */
	@Override
	public void onPause() {
		super.onPause();
		mUserOverlay.disableMyLocation();
//		removeOverlay(mUserOverlay);
	}

	/**
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (mUserOverlay != null) {
			mUserOverlay.enableMyLocation();
//			addOverlay(mUserOverlay);
		}
	}

	/**
	 * reorders the overlays to the UserOverlay always on top
	 * 
	 * @author ricky barrette
	 */
//	public void reorderOverlays() {
//		getMap().getOverlays().remove(mUserOverlay);
//		getMap().getOverlays().add(mUserOverlay);
//	}

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
	public void setDestination(final LatLng destination) {
		mUserOverlay.setDestination(destination);
	}

	/**
	 * @param listener
	 * @author ricky barrette
	 */
	public void setGeoPointLocationListener(final LatLngListener listener) {
		mLatLngListener = listener;
	}
}