/**
 * SkyHookTesting.java
 * @date Mar 1, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.debug;

/**
 * This enum will be used to select the testing level
 * 
 * @author ricky barrette
 */
public enum SkyHookRegistrationBehavior {

	/**
	 * Used to force SkyHookRegistration.getUserName to behave normally
	 */
	NORMAL,

	/**
	 * Used to force SkyHookRegistration.getUserName to return the testing user
	 * name
	 */
	USE_TESTING_USERNAME,

	/**
	 * Used to force SkyHookRegistration.getUserName to return null
	 */
	RETURN_NULL;

}