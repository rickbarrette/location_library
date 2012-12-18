/**
 * GeoPointLocationListener.java
 * @author ricky barrette
 * @date Oct 2, 2010
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
package com.TwentyCodes.android.location;

import com.google.android.maps.GeoPoint;

/**
 * this interface will be used to interface with skyhook sdk with the rest of
 * the application
 * 
 * @author ricky barrette
 */
public interface GeoPointLocationListener {

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
	public void onLocationChanged(GeoPoint point, int accuracy);
}
