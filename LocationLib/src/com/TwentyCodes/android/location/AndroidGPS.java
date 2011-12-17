/**
 * AndroidGPS.java
 * @date Feb 3, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.TwentyCodes.android.SkyHook.SkyHook;
import com.TwentyCodes.android.debug.Debug;
import com.google.android.maps.GeoPoint;

/**
 * This class will be used for gathering location using android's location services
 * @author ricky barrette
 */
public class AndroidGPS implements LocationListener {
	
	private static final String TAG = "AndroidGPS";
	private LocationManager mLocationManager;
	private GeoPointLocationListener mListener;
	private LocationListener mLocationListener;
	
	/**
	 * Creates a new SkyHookFallback
	 * @author ricky barrette
	 */
	public AndroidGPS(Context context) {
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * Remove updates from androids location services
	 * @author ricky barrette
	 */
	public void disableLocationUpdates(){
		if(Debug.DEBUG)
			Log.d(TAG, "disableLocationUpdates()");
		mListener = null;
		mLocationManager.removeUpdates(this);
	}

	/**
	 * Attempts to enable periodic location updates
	 * @param listener
	 * @author ricky barrette
	 */
	public void enableLocationUpdates(LocationListener listener) {
		if(Debug.DEBUG)
			Log.d(SkyHook.TAG, "enableLocationUpdates()");
		if(mLocationListener == null){
			mLocationListener = listener;
			requestUpdates();
		}	
	}

	/**
	 * request periodic location updates from androids location services
	 * @author ricky barrette
	 */
	public void enableLocationUpdates(GeoPointLocationListener listener) {
		if(Debug.DEBUG)
			Log.d(SkyHook.TAG, "enableLocationUpdates()");
		if (mListener == null) {
			mListener = listener;
			requestUpdates();
		}
	}

	/**
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 * @param location
	 * @author ricky barrette
	 */
	@Override
	public void onLocationChanged(Location location) {
		if(mListener != null)
			mListener.onLocationChanged(new GeoPoint( (int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6)), (int) location.getAccuracy());
		
		if(mLocationListener != null){
			mLocationListener.onLocationChanged(location);
		}
	}
	
	/**
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 * @param arg0
	 * @author ricky barrette
	 */
	@Override
	public void onProviderDisabled(String arg0) {
		// UNUSED

	}

	/**
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 * @param arg0
	 * @author ricky barrette
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		// UNUSED
	}
	
	/**
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @author ricky barrette
	 */
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// UNUSED
	}

	/**
	 * Request updates from android location services
	 * @author ricky barrette
	 */
	private void requestUpdates() {
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		try {
			mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			/* We do no handle this exception as it is caused if the android version is < 1.6. since the PASSIVE_PROVIDER call is not required
			 * to function we can ignore it.
			 */
		}
	}

}