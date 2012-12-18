/**
 * BaseLocationReceiver.java
 * @author ricky barrette
 * @date Oct 18, 2010
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