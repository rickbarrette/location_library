/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Oct 2, 2010
 */
package com.TwentyCodes.android.overlays;

import android.content.Context;

import com.TwentyCodes.android.SkyHook.SkyHook;
import com.google.android.maps.MapView;

/**
 * this class will be used to display the users location on the map using skyhook's call back methods
 * @author ricky barrette
 */
public class SkyHookUserOverlay extends BaseUserOverlay{

	private final SkyHook mSkyHook;

	public SkyHookUserOverlay(final MapView mapView, final Context context) {
		super(mapView, context);
		mSkyHook = new SkyHook(context);
	}

	/**
	 * Construct a new SkyHookUserOverlay
	 * @param mapView
	 * @param context
	 * @param followUser
	 * @author ricky barrette
	 */
	public SkyHookUserOverlay(final MapView mapView, final Context context, final boolean followUser) {
		super(mapView, context, followUser);
		mSkyHook = new SkyHook(context);
	}

	@Override
	public void onFirstFix(final boolean isFistFix) {
		// unused
	}

	/**
	 * Called when the location provider needs to be disabled
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.overlays.BaseUserOverlay#onMyLocationDisabled()
	 */
	@Override
	public void onMyLocationDisabled() {
		mSkyHook.removeUpdates();
	}

	/**
	 * Called when the location provider needs to be enabled
	 * (non-Javadoc)
	 * @see com.TwentyCodes.android.overlays.BaseUserOverlay#onMyLocationEnabled()
	 */
	@Override
	public void onMyLocationEnabled() {
		mSkyHook.setLocationListener(this);
		mSkyHook.getUpdates();
	}

}