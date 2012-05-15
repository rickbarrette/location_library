/**
 * PassiveLocationListener.java
 * @date May 15, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.TwentyCodes.android.debug.LocationLibraryConstants;

/**
 * A convenience class for requesting passive location updates
 * @author ricky barrette
 */
public class PassiveLocationListener {

	/**
	 * A convenience method for requesting passive location updates
	 * @param context
	 * @author ricky barrette
	 */
	public static final void requestPassiveLocationUpdates(final Context context){
		if (LocationLibraryConstants.SUPPORTS_FROYO) {
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            final Intent passiveIntent = new Intent(context, PassiveLocationChangedReceiver.class);
            final PendingIntent locationListenerPassivePendingIntent = PendingIntent.getBroadcast(context, 0, passiveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListenerPassivePendingIntent);
        }
	}
}