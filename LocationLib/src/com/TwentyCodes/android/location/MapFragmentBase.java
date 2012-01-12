/**
 * MapFragment.java
 * @date Jan 7, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.maps.GeoPoint;

/**
 * This map fragment will maintain a map view and all its functions
 * 
 * @author ricky barrette
 */
public abstract class MapFragmentBase extends Fragment {

	private MapView mMapView;
	
	/**
	 * Creates a new MapFragment
	 * @author ricky barrette
	 */
	public MapFragmentBase() {
		super();
	}

	/**
	 * Called when the fragment view is first created
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_fragment, container, false);
		
		mMapView = (MapView) view.findViewById(R.id.mapview);
		mMapView.setClickable(true);

		onMapViewCreate(mMapView);
		
		return view;
	}
	
	/**
	 * Called when the mapview has been initialized. here you want to init and add your custom overlays
	 * @param map
	 * @author ricky barrette
	 */
	public abstract void onMapViewCreate(MapView map);
	
	/**
	 * Removes an overlay from the mapview
	 * @param overlay
	 * @author ricky barrette
	 */
	protected void removeOverlay(Object overlay){
		mMapView.getOverlays().remove(overlay);
	}
	
	/**
	 * Sets the center of the map to the provided point
	 * @param point
	 * @author ricky barrette
	 */
	public void setMapCenter(GeoPoint point){
		mMapView.getController().setCenter(point);
	}
}