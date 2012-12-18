/**
 * MapView.java
 * @author ricky barrette
 * @date Oct 10, 2010
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

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.TwentyCodes.android.debug.Debug;

/**
 * We use this MapView Because it has double tap zoom capability and exception
 * handling
 * 
 * @author ricky barrette
 */
public class MapView extends com.google.android.maps.MapView {

	private static final String TAG = "MapView";
	private long mLastTouchTime;
	private boolean mDoubleTapZoonEnabled = true;

	/**
	 * @param context
	 * @param attrs
	 * @author ricky barrette
	 */
	public MapView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 * @author ricky barrette
	 */
	public MapView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param apiKey
	 * @author ricky barrette
	 */
	public MapView(final Context context, final String apiKey) {
		super(context, apiKey);
	}

	/**
	 * We will override the draw method to help prevent issues (non-Javadoc)
	 * 
	 * @see android.view.View#draw(android.graphics.Canvas)
	 * @author ricky barrette
	 */
	@Override
	public void draw(final Canvas canvas) {
		try {
			if (getZoomLevel() >= 21)
				getController().setZoom(20);
			super.draw(canvas);
		} catch (final Exception ex) {
			// getController().setCenter(this.getMapCenter());
			// getController().setZoom(this.getZoomLevel() - 2);
			if (Debug.DEBUG)
				Log.d(TAG, "Internal error in MapView:" + Log.getStackTraceString(ex));
		}
	}

	/**
	 * @return the isDoubleTapZoonEnabled
	 * @author ricky barrette
	 */
	public boolean getDoubleTapZoonEnabled() {
		return mDoubleTapZoonEnabled;
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			final long thisTime = System.currentTimeMillis();
			if (mDoubleTapZoonEnabled && thisTime - mLastTouchTime < 250) {
				// Double tap
				getController().zoomInFixing((int) ev.getX(), (int) ev.getY());
				mLastTouchTime = -1;
			} else
				// Too slow
				mLastTouchTime = thisTime;
		}

		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * @param isDoubleTapZoonEnabled
	 *            the isDoubleTapZoonEnabled to set
	 * @author ricky barrette
	 */
	public void setDoubleTapZoonEnabled(final boolean isDoubleTapZoonEnabled) {
		mDoubleTapZoonEnabled = isDoubleTapZoonEnabled;
	}
}