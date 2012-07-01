/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Dec 28, 2010
 */
package com.TwentyCodes.android.overlays;

import android.content.Context;

import com.TwentyCodes.android.location.AndroidGPS;
import com.google.android.maps.MapView;

/**
 * This is the standard version of the UserOverlay.
 * @author ricky barrette
 */
public class UserOverlay extends BaseUserOverlay{

	private final AndroidGPS mAndroidGPS;

	public UserOverlay(final MapView mapView, final Context context) {
		super(mapView, context);
		mAndroidGPS = new AndroidGPS(context);
	}

	public UserOverlay(final MapView mapView, final Context context, final boolean followUser) {
		super(mapView, context, followUser);
		mAndroidGPS = new AndroidGPS(context);
	}

	@Override
	public void onFirstFix(final boolean isFistFix) {
		// unused
	}

	@Override
	public void onMyLocationDisabled() {
		mAndroidGPS.disableLocationUpdates();
	}

	@Override
	public void onMyLocationEnabled() {
		mAndroidGPS.enableLocationUpdates(this);
	}

}