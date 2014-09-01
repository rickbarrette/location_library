/**
 * OnLocationSelectedListener.java
 * @author ricky barrette
 * 
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