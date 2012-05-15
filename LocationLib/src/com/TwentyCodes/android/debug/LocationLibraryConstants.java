/**
 * Debug.java
 * @date Mar 1, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.debug;

import android.hardware.SensorManager;

/**
 * This class will be used to set the Location Library Constants
 * @author ricky barrette
 */
public final class LocationLibraryConstants {
	
	/**
	 * Sets the default SkyHook Registration Behavior used by SkyHookRegistration.getUserName()
	 * @author ricky barrette
	 */
	public static final SkyHookRegistrationBehavior DEFAULT_REGISTRATION_BEHAVIOR = SkyHookRegistrationBehavior.NORMAL;

	/**
	 * Sets the default compass sensor update interval
	 * @author ricky barrette
	 */
	public static final int COMPASS_UPDATE_INTERVAL = SensorManager.SENSOR_DELAY_NORMAL;

	/**
	 * The maximum running time for a single shot location service
	 * @author ricky barrette
	 */
	public static final long MAX_LOCATION_SERVICE_RUN_TIME = 60000l;
	
	/**
	 * Forces single shot location services to return the first location
	 * @author ricky barrette
	 */
	public static final boolean REPORT_FIRST_LOCATION = false;
	
	/**
	 * Minimum Required accuracy to report
	 * @author ricky barrette
	 */
	public static final int MINIMUM_REQUIRED_ACCURACY = 50;
	
	public static final boolean SUPPORTS_FROYO;
	
	public static final boolean SUPPORTS_GINGERBREAD;
	
	static{
		SUPPORTS_FROYO = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO;
		
		SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
	}
}