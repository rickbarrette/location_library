/**
 * LocationLibraryConstants.java
 * @date Mar 1, 2011
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
package com.TwentyCodes.android.debug;

import android.app.AlarmManager;
import android.hardware.SensorManager;
import android.location.LocationManager;

/**
 * This class will be used to set the Location Library Constants
 * 
 * @author ricky barrette
 */
public final class LocationLibraryConstants {

	static {
		SUPPORTS_FROYO = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO;

		SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * Sets the default compass sensor update interval
	 * 
	 * @author ricky barrette
	 */
	public static final int COMPASS_UPDATE_INTERVAL = SensorManager.SENSOR_DELAY_NORMAL;

	/**
	 * The maximum running time for a single shot location service
	 * 
	 * @author ricky barrette
	 */
	public static final long MAX_LOCATION_SERVICE_RUN_TIME = 60000l;

	/**
	 * Forces single shot location services to return the first location
	 * 
	 * @author ricky barrette
	 */
	public static final boolean REPORT_FIRST_LOCATION = false;

	/**
	 * Minimum Required accuracy to report
	 * 
	 * @author ricky barrette
	 */
	public static final int MINIMUM_REQUIRED_ACCURACY = 100;

	public static final boolean SUPPORTS_FROYO;

	public static final boolean SUPPORTS_GINGERBREAD;

	public static final String INTENT_ACTION_UPDATE = "TwentyCodes.intent.action.LocationUpdate";

	public static final String INTENT_EXTRA_LOCATION_CHANGED = LocationManager.KEY_LOCATION_CHANGED;

	public static final String INTENT_EXTRA_LOCATION_ATUO = "RockBarrette.action.LocationAuto";

	/**
	 * Used to tell the service how accurate of a location you want reported
	 */
	public static final String INTENT_EXTRA_REQUIRED_ACCURACY = "required_accuracy";

	/**
	 * used if the INTENT_EXTRA_PERIOD_BETWEEN_UPDATES is present, but contains
	 * no data
	 */
	public static final long FAIL_SAFE_UPDATE_INVERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	private static final int UPDATE_INTERVAL_IN_SECONDS = 30;
	// Update frequency in milliseconds
	public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 30;
	// A fast frequency ceiling in milliseconds
	public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
}