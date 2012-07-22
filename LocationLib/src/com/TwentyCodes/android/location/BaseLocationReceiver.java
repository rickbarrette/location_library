/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Oct 18, 2010
 */
package com.TwentyCodes.android.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

/**
 * this abstract class will be used as a for classes wishing to be a receiver of
 * location updates from the location services
 * 
 * @author ricky barrette
 */
public abstract class BaseLocationReceiver extends BroadcastReceiver {

	public Context mContext;

	/**
	 * called when a location update is received
	 * 
	 * @param parcelableExtra
	 * @author ricky barrette
	 */
	public abstract void onLocationUpdate(Location location);

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 *      android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		mContext = context;
		final String key = LocationManager.KEY_LOCATION_CHANGED;
		if (intent.hasExtra(key))
			onLocationUpdate((Location) intent.getExtras().get(key));
	}
}