/**
 * PassiveLocationChangedReceiver.java
 * @date May 15, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

/**
 * @author ricky barrette
 */
public abstract class PassiveLocationChangedReceiver extends BroadcastReceiver {

	protected Context mContext;

	/**
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		final String key = LocationManager.KEY_LOCATION_CHANGED;
		if (intent.hasExtra(key))
			onLocationUpdate((Location)intent.getExtras().get(key));
	}
	
	/**
	 * called when a location update is received
	 * @param parcelableExtra
	 * @author ricky barrette
	 */
	public abstract void onLocationUpdate(Location location);
}