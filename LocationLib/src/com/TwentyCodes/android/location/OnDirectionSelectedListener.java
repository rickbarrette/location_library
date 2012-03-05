/**
 * OnDirectionSelectedListener.java
 * @date Mar 5, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import com.google.android.maps.GeoPoint;

/**
 * A simple interfrace for a directions list fragment
 * @author ricky barrette
 */
public interface OnDirectionSelectedListener {
	
	/**
	 * Called when the user selects a direction from a directions list
	 * @param point
	 * @author ricky barrette
	 */
	public void onDirectionSelected(GeoPoint point);

}
