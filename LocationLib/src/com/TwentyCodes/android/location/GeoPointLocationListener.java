/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Oct 2, 2010
 */
package com.TwentyCodes.android.location;

import com.google.android.maps.GeoPoint;

/**
 * this interface will be used to interface with skyhook sdk with the rest of the application
 * @author ricky barrette
 */
public interface GeoPointLocationListener {
	
	/**
	 * Called when the location has changed
	 * @param point
	 * @param accuracy
	 * @author ricky barrette
	 */
	public void onLocationChanged(GeoPoint point, int accuracy);
	
	/**
	 * Called when first fix is aquired
	 * @param isFirstFix
	 * @author ricky barrette
	 */
	public void onFirstFix(boolean isFirstFix);
}
