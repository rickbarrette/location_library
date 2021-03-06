/**
 * AndroidGPS.java
 * @date Feb 3, 2011
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
package com.TwentyCodes.android.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.TwentyCodes.android.debug.Debug;
import com.google.android.gms.maps.model.LatLng;

/**
 * This class will be used for gathering location using android's location
 * services
 * 
 * @author ricky barrette
 */
public class AndroidGPS implements LocationListener {

	private static final String TAG = "AndroidGPS";
	private final LocationManager mLocationManager;
	private LatLngListener mListener;
	private LocationListener mLocationListener;
	private boolean isFirstFix;

	/**
	 * Creates a new SkyHookFallback
	 * 
	 * @author ricky barrette
	 */
	public AndroidGPS(final Context context) {
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		isFirstFix = true;
	}

	/**
	 * Remove updates from androids location services
	 * 
	 * @author ricky barrette
	 */
	public void disableLocationUpdates() {
		if (Debug.DEBUG)
			Log.d(TAG, "disableLocationUpdates()");
		mListener = null;
		mLocationManager.removeUpdates(this);
		isFirstFix = true;
	}

	/**
	 * request periodic location updates from androids location services
	 * 
	 * @author ricky barrette
	 */
	public void enableLocationUpdates(final LatLngListener listener) {
		if (Debug.DEBUG)
			Log.d(TAG, "enableLocationUpdates()");
		if (mListener == null) {
			mListener = listener;
			requestUpdates();
		}
	}

	/**
	 * Attempts to enable periodic location updates
	 * 
	 * @param listener
	 * @author ricky barrette
	 */
	public void enableLocationUpdates(final LocationListener listener) {
		if (Debug.DEBUG)
			Log.d(TAG, "enableLocationUpdates()");
		if (mLocationListener == null) {
			mLocationListener = listener;
			requestUpdates();
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 * @param location
	 * @author ricky barrette
	 */
	@Override
	public void onLocationChanged(final Location location) {
		if (mListener != null) {
			mListener.onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()), (int) location.getAccuracy());
			mListener.onFirstFix(isFirstFix);
		}

		if (mLocationListener != null)
			mLocationListener.onLocationChanged(location);

		isFirstFix = false;
	}

	/**
	 * (non-Javadoc)
	 * 
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
	 * 
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
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 *      int, android.os.Bundle)
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
	 * 
	 * @author ricky barrette
	 */
	private void requestUpdates() {
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

}