/**
 * CompassListener.java
 * @date Mar 2, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

/**
 * A simple listener interface to get updates from CompassSensor
 * @author ricky barrette
 */
public interface CompassListener {
	
	/**
	 * Called when there is an update from the Compass Sensor
	 * @param bearing
	 * @author ricky barrette
	 */
	public void onCompassUpdate(float bearing);
}