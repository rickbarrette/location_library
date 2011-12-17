/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Oct 10, 2010
 */
package com.TwentyCodes.android.location;

import com.TwentyCodes.android.debug.Debug;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * We use this MapView Because it has double tap zoom capability and exception handling
 * @author ricky barrette
 */
public class MapView extends com.google.android.maps.MapView {

	private static final String TAG = "MapView";
	private long mLastTouchTime;
	private boolean mDoubleTapZoonEnabled = true;

	/**
	 * @param context
	 * @param apiKey
	 * @author ricky barrette
	 */
	public MapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	/**
	 * @param context
	 * @param attrs
	 * @author ricky barrette
	 */
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 * @author ricky barrette
	 */
	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			long thisTime = System.currentTimeMillis();
			if (this.mDoubleTapZoonEnabled && thisTime - mLastTouchTime < 250) {
				// Double tap
				this.getController().zoomInFixing((int) ev.getX(), (int) ev.getY());
				mLastTouchTime = -1;
			} else {
				// Too slow
				mLastTouchTime = thisTime;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}
	
	/**
	 * We will override the draw method to help prevent issues
	 * (non-Javadoc)
	 * @see android.view.View#draw(android.graphics.Canvas)
	 * @author ricky barrette
	 */
	 @Override
	    public void draw(Canvas canvas) {
	        try {
	            if(this.getZoomLevel() >= 21) {
	                this.getController().setZoom(20);
	            }
	            super.draw(canvas);
	        }
	        catch(Exception ex) {           
//	            getController().setCenter(this.getMapCenter());
//	            getController().setZoom(this.getZoomLevel() - 2);
	            if(Debug.DEBUG)
	            	Log.d(TAG, "Internal error in MapView:" + Log.getStackTraceString(ex));
	        }
	    }

	/**
	 * @param isDoubleTapZoonEnabled the isDoubleTapZoonEnabled to set
	 * @author ricky barrette
	 */
	public void setDoubleTapZoonEnabled(boolean isDoubleTapZoonEnabled) {
		this.mDoubleTapZoonEnabled = isDoubleTapZoonEnabled;
	}

	/**
	 * @return the isDoubleTapZoonEnabled
	 * @author ricky barrette
	 */
	public boolean getDoubleTapZoonEnabled() {
		return mDoubleTapZoonEnabled;
	}
}