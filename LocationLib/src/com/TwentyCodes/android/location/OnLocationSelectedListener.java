package com.TwentyCodes.android.location;

import com.google.android.gms.maps.model.LatLng;

/**
 * This interface will be used to pass the selected location from the dialogs to
 * the listening instance
 * 
 * @author ricky barrette
 */
public interface OnLocationSelectedListener {

	public void onLocationSelected(LatLng point);
}