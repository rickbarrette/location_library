/**
 * MapFragment.java
 * @date Jan 7, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.TwentyCodes.android.location.MapView;
import com.TwentyCodes.android.location.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

/**
 * This map fragment will maintain a map view and all its functions
 * 
 * @author ricky barrette
 */
public abstract class BaseMapFragment extends Fragment {

	private MapView mMapView;
	private boolean isGPSDialogEnabled;
	private ProgressBar mProgress;
	
	/**
	 * Creates a new MapFragment
	 * @author ricky barrette
	 */
	public BaseMapFragment() {
		super();
	}
	
	public void addOverlay(Overlay overlay){
		mMapView.getOverlays().add(overlay);
	}
	
	/**
	 * changes the map mode
	 * @author ricky barrette
	 */
	public void changeMapMode() {
		mMapView.setSatellite(!mMapView.isSatellite());
	}

	/**
	 * @return mapview
	 * @author ricky barrette
	 */
	public MapView getMap(){
		return mMapView;
	}
	
	/**
	 * Called when the fragment view is first created
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.base_map_fragment, container, false);
		
		mMapView = (MapView) view.findViewById(R.id.mapview);
		mMapView.setClickable(true);
		
		mProgress = (ProgressBar) view.findViewById(R.id.mapProgressBar);

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
	public void removeOverlay(Object overlay){
		mMapView.getOverlays().remove(overlay);
	}
	
	/**
	 * Sets the center of the map to the provided point
	 * @param point
	 * @author ricky barrette
	 */
	public boolean setMapCenter(GeoPoint point){
		if(point == null)
			return false;
		mMapView.getController().setCenter(point);
		return true;
	}
	
	/**
	 * Disables the Acquiring GPS dialog
	 * @author ricky barrette
	 */
	public void disableGPSProgess(){
		isGPSDialogEnabled = false;
		mProgress.setVisibility(View.GONE);
	}
	
	/**
     * Enables the Acquiring GPS dialog if the location has not been acquired
     * 
     * @author ricky barrette
     */
    public void enableGPSProgess(){
    	isGPSDialogEnabled = true;
    	mProgress.setVisibility(View.VISIBLE);
    }
    
    public boolean isGPSProgessShowing(){
    	return isGPSDialogEnabled;
    }
}