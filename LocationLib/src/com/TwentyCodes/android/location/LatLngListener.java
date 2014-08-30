/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Oct 2, 2010
 */
package com.TwentyCodes.android.location;

import com.google.android.gms.maps.model.LatLng;

/**
 * this interface will be used to interface with the GPS sdk with the rest of
 * the application
 * 
 * @author ricky barrette
 */
public interface LatLngListener {

	/**
	 * Called when first fix is aquired
	 * 
	 * @param isFirstFix
	 * @author ricky barrette
	 */
	public void onFirstFix(boolean isFirstFix);

	/**
	 * Called when the location has changed
	 * 
	 * @param point
	 * @param accuracy
	 * @author ricky barrette
	 */
	public void onLocationChanged(LatLng point, int accuracy);
}
