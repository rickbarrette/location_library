/**
 * UserOverlayMapFragmentBase.java
 * @date Jan 12, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.SkyHook;

import com.TwentyCodes.android.location.CompassListener;
import com.TwentyCodes.android.location.GeoPointLocationListener;
import com.TwentyCodes.android.location.MapFragmentBase;
import com.TwentyCodes.android.location.MapView;
import com.google.android.maps.GeoPoint;

/**
 * This is a MapFragment that maintains the SkyHookUserOverlay
 * 
 * TODO acquiring gps dialog
 * @author ricky barrette
 */
public class SkyHoookUserOverlayMapFragment extends MapFragmentBase implements GeoPointLocationListener, CompassListener{

	private SkyHookUserOverlay mUserOverlay;
	private GeoPointLocationListener mGeoPointLocationListener;
	private CompassListener mCompassListener;

	/**
	 * Creates a new UserOverlayMapFragment
	 * @author ricky barrette
	 */
	public SkyHoookUserOverlayMapFragment() {
		super();
	}

	/**
	 * @return the users current location
	 * @author ricky barrette
	 */
	public GeoPoint getUserLocation() {
		return mUserOverlay.getUserLocation();
	}

	/**
	 * Called when the compass is updated
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.location.CompassListener#onCompassUpdate(float)
	 */
	@Override
	public void onCompassUpdate(float bearing) {
		if(mCompassListener != null)
			mCompassListener.onCompassUpdate(bearing);
	}

	/**
	 * Called when has a location to report
	 * @author ricky barrette
	 */
	@Override
	public void onLocationChanged(GeoPoint point, int accuracy) {
		if(mGeoPointLocationListener != null)
			mGeoPointLocationListener.onLocationChanged(point, accuracy);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.location.MapFragmentBase#onMapViewCreate(com.TwentyCodes.android.location.MapView)
	 */
	@Override
	public void onMapViewCreate(MapView map) {
		mUserOverlay = new SkyHookUserOverlay(map, this.getActivity().getApplicationContext());
		mUserOverlay.registerListener(this);
		mUserOverlay.setCompassListener(this);
		mUserOverlay.enableCompass();
		mUserOverlay.disableGPSDialog();
		mUserOverlay.followUser(true);
		
		map.getOverlays().add(mUserOverlay);
	}

	/**
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.location.MapFragmentBase#onPause()
	 */
	@Override
	public void onPause() {
		mUserOverlay.disableMyLocation();
		removeOverlay(mUserOverlay);
		super.onPause();
	}

	/**
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.location.MapFragmentBase#onResume()
	 */
	@Override
	public void onResume() {
		if(mUserOverlay != null)
			mUserOverlay.enableMyLocation();
		super.onResume();
	}

	/**
	 * @param listener
	 * @author ricky barrette
	 */
	public void setCompassListener(CompassListener listener){
		mCompassListener = listener;
	}
	
	/**
	 * Sets the destination for the compass to point to
	 * @param destination
	 * @author ricky barrette
	 */
	public void setDestination(GeoPoint destination){
		mUserOverlay.setDestination(destination);
	}
	
	/**
	 * @param listener
	 * @author ricky barrette
	 */
	public void setGeoPointLocationListener(GeoPointLocationListener listener){
		mGeoPointLocationListener = listener;
	}

}
