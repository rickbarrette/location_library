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
	private final LocationManager mLocationManager;
	private GeoPointLocationListener mListener;
	private LocationListener mLocationListener;
	private boolean isFirstFix;

	/**
	 * Creates a new SkyHookFallback
	 * @author ricky barrette
	 */
	public AndroidGPS(final Context context) {
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		isFirstFix = true;
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
		isFirstFix = true;
	}

	/**
	 * request periodic location updates from androids location services
	 * @author ricky barrette
	 */
	public void enableLocationUpdates(final GeoPointLocationListener listener) {
		if(Debug.DEBUG)
			Log.d(SkyHook.TAG, "enableLocationUpdates()");
		if (mListener == null) {
			mListener = listener;
			requestUpdates();
		}
	}

	/**
	 * Attempts to enable periodic location updates
	 * @param listener
	 * @author ricky barrette
	 */
	public void enableLocationUpdates(final LocationListener listener) {
		if(Debug.DEBUG)
			Log.d(SkyHook.TAG, "enableLocationUpdates()");
		if(mLocationListener == null){
			mLocationListener = listener;
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
	public void onLocationChanged(final Location location) {
		if(mListener != null) {
			mListener.onLocationChanged(new GeoPoint( (int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6)), (int) location.getAccuracy());
			mListener.onFirstFix(isFirstFix);
		}

		if(mLocationListener != null)
			mLocationListener.onLocationChanged(location);

		isFirstFix = false;
	}

	/**
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 * @param arg0
	 * @author ricky barrette
	 */
	@Override
	public void onProviderDisabled(final String arg0) {
		// UNUSED

	}

	/**
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 * @param arg0
	 * @author ricky barrette
	 */
	@Override
	public void onProviderEnabled(final String arg0) {
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
	public void onStatusChanged(final String arg0, final int arg1, final Bundle arg2) {
		// UNUSED
	}

	/**
	 * Request updates from android location services
	 * @author ricky barrette
	 */
	private void requestUpdates() {
		try {
			mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
			/* We do no handle this exception as it is caused if the android version is < 1.6. since the PASSIVE_PROVIDER call is not required
			 * to function we can ignore it.
			 */
		}
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

}