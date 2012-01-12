/**
 * UserOverlayMapFragment.java
 * @date Jan 12, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import com.google.android.maps.GeoPoint;

/**
 * This is a MapFragment that maintains the UserOverlay
 * 
 * TODO acquiring gps dialog
 * @author ricky barrette
 */
public class UserOverlayMapFragment extends MapFragmentBase implements GeoPointLocationListener, CompassListener{

	private UserOverlay mUserOverlay;
	private GeoPointLocationListener mGeoPointLocationListener;
	private CompassListener mCompassListener;

	/**
	 * Creates a new UserOverlayMapFragment
	 * @author ricky barrette
	 */
	public UserOverlayMapFragment() {
		super();
	}

	/**
	 * disables the GPS dialog
	 * @author ricky barrette
	 */
	public void disableGPSDialog(){
		mUserOverlay.disableGPSDialog();
	}
	
	/**
	 * enables the GPS dialog
	 * @author ricky barrette
	 */
	public void enableGPSDialog(){
		mUserOverlay.enableGPSDialog();
	}
	
	/**
	 * Tells the useroverlay to pan the map to follow the user
	 * @param followUser
	 * @author ricky barrette
	 */
	public void followUser(boolean followUser){
		mUserOverlay.followUser(followUser);
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
	 * Called when skyhook has a location to report
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
		mUserOverlay = new UserOverlay(map, this.getActivity().getApplicationContext());
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
		super.onPause();
		mUserOverlay.disableMyLocation();
		removeOverlay(mUserOverlay);
	}

	/**
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.location.MapFragmentBase#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(mUserOverlay != null)
			mUserOverlay.enableMyLocation();
	}
	
	/**
	 * reorders the overlays to the UserOverlay always on top
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
	public void setCompassDrawables(int needleResId, int backgroundResId, int x, int y){
		mUserOverlay.setCompassDrawables(needleResId, backgroundResId, x, y);
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
