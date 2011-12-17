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

/**
 * this abstract class will be used as a for classes wishing to be a receiver of location updates from the location services
 * @author ricky barrette
 */
public abstract class LocationReceiver extends BroadcastReceiver {
	
	public static final String INTENT_EXTRA_ACTION_UPDATE = "TwentyCodes.intent.action.LocationUpdate";
	public static final String INTENT_EXTRA_LOCATION_PARCEL = "location_parcel";
	public Context mContext;

	/**
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 * @param contextonBind
	 * @param intent
	 * @author ricky barrette
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		if(intent.getParcelableExtra(INTENT_EXTRA_LOCATION_PARCEL) != null){
			Location location = intent.getParcelableExtra(INTENT_EXTRA_LOCATION_PARCEL);
			onLocationUpdate(location);
		}
	}

	/**
	 * called when a location update is received
	 * @param parcelableExtra
	 * @author ricky barrette
	 */
	public abstract void onLocationUpdate(Location location);

}