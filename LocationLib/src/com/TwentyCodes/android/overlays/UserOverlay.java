/**
 * UserOverlay.java
 * @author ricky barrette
 * @date Dec 28, 2010
 * 
 * Copyright 2012 Richard Barrette 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License
 */
package com.TwentyCodes.android.overlays;

import android.content.Context;

import com.TwentyCodes.android.location.AndroidGPS;
import com.google.android.maps.MapView;

/**
 * This is the standard version of the UserOverlay.
 * 
 * @author ricky barrette
 */
public class UserOverlay extends BaseUserOverlay {

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