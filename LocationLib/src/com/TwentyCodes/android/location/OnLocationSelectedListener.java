package com.TwentyCodes.android.location;

import com.google.android.maps.GeoPoint;

/**
 * This interface will be used to pass the selected location from the dialogs to
 * the listening instance
 * 
 * @author ricky barrette
 */
public interface OnLocationSelectedListener {

	public void onLocationSelected(GeoPoint point);
}