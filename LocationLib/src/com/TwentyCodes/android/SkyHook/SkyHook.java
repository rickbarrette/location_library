/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Oct 2, 2010
 */
package com.TwentyCodes.android.SkyHook;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.TwentyCodes.android.debug.Debug;
import com.TwentyCodes.android.location.AndroidGPS;
import com.TwentyCodes.android.location.GeoPointLocationListener;
import com.google.android.maps.GeoPoint;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.XPS;

/**
 * this calls will be  used to create skyhook object that uses an listener interface to interact with the rest of location ringer
 * @author ricky barrette
 */
public class SkyHook implements GeoPointLocationListener{
	
	public static final String TAG = "Skyhook";
	public static final String USERNAME = "cjyh95q32gsc";
	public static final String USERNAME_FOR_TESTING = "twentycodes";
	public static final String REALM = "TwentyCodes";
	public static final int LOCATION_MESSAGE = 1;
    public static final int ERROR_MESSAGE = 2;
    public static final int DONE_MESSAGE = 3;
    private final XPScallback mXPScallback = new XPScallback();
    private final XPS mXps;
    private final Context mContext;
	private GeoPointLocationListener mListener;
    private long mPeriod = 0l; //period is in milliseconds for periodic updates
    private int mIterations = 0;
	private WPSAuthentication mWPSAuthentication;
	private Handler mHandler;
	private boolean isPeriodicEnabled;
	private boolean hasLocation;
	protected AndroidGPS mSkyHookFallback = null;
	protected long mFallBackDelay = 5000l;
	private boolean isFallBackScheduled = false;
	private boolean isEnabled = false;
	private boolean isUnauthorized = false;
	
	/*
	 * this runnable will be used to check if we have location from skyhook,
	 * if we dont, then we will us android's location services to fall back on.
	 */
	private final Runnable mFallBack = new Runnable() {
		public void run() {
			mHandler.removeCallbacks(mFallBack);
			Log.d(TAG,"skyhook, "+ (hasLocation ? "is" : "isn't") +" working!");
			
            if((! hasLocation) && (mSkyHookFallback == null) && isEnabled){
            	Log.d(TAG,"falling back on android");
            	mSkyHookFallback  = new AndroidGPS(mContext);
            	mSkyHookFallback.enableLocationUpdates(SkyHook.this);
            	/*
            	 * Schedule another check, if skyhook is still enabled
            	 */
            	if(mXps != null)
            		mHandler.postDelayed(mFallBack, mFallBackDelay );
            	
            } else {
            	Log.d(TAG,"already fell back on android");
            	if(mSkyHookFallback != null) {
            		Log.d(TAG,"got location, picking up the slack");
            		mSkyHookFallback.disableLocationUpdates();
            		mSkyHookFallback = null;
            	} 
            	isFallBackScheduled = false;
            }
        }
    };
	
    /*
     * this runnable keeps skyhook working!
     */
	private final Runnable mPeriodicUpdates = new Runnable() {
		public void run() {
			if(Debug.DEBUG)
				Log.d(TAG,"geting location");
            mXps.getXPSLocation(mWPSAuthentication, mIterations, XPS.EXACT_ACCURACY, mXPScallback);
        }
    };
    
	private class XPScallback implements WPSPeriodicLocationCallback  {
    	@Override
		public void done() {
            mHandler.sendMessage(mHandler.obtainMessage(DONE_MESSAGE));
		}

		@Override
		public WPSContinuation handleError(WPSReturnCode error) {
            mHandler.sendMessage(mHandler.obtainMessage(ERROR_MESSAGE, error));
    		return WPSContinuation.WPS_CONTINUE;
		}

		@Override
		public WPSContinuation handleWPSPeriodicLocation(WPSLocation location) {
			mHandler.sendMessage(mHandler.obtainMessage(LOCATION_MESSAGE, location));
    		return WPSContinuation.WPS_CONTINUE;
		}
    }
	
	/**
	 * Constructors a new skyhook object
	 * @param context
	 * @author ricky barrette
	 */
	public SkyHook(Context context) {
		mXps = new XPS(context);
		mContext = context;
		// initialize the Handler which will display location data
        // in the text view. we use a Handler because UI updates
        // must occur in the UI thread
        setUIHandler();
	}
	
	/**
	 * Constructors a new skyhook object
	 * @param context
	 * @param period between location updates in milliseconds
	 * @author ricky barrette
	 */
	public SkyHook(Context context, long period) {
		this(context);
		mPeriod = period;
	}
	
	/**
	 * request current user location, note that the listeners onLocationChanged() will be call multiple times.
	 * updates will stop once an accurate location is determined.
	 * @author Ricky Barrette
	 */
	public void getLoctaion(){
		Log.d(TAG,"getLocation()");
		if (mListener != null){
			mWPSAuthentication = new WPSAuthentication(SkyHookRegistration.getUserName(mContext), REALM);
			mHandler.post(mPeriodicUpdates);
		}
	}
	
	/**
	 * Attempts to register the the listener for periodic updates
	 * @author Ricky Barrette
	 */
	public void getUpdates(){
		Log.d(TAG,"getUpdates()");
		if (mListener != null) {
			
			if(Debug.DEBUG)
				Log.i(TAG, "username: " + SkyHookRegistration.getUserName(mContext));
			
			mWPSAuthentication = new WPSAuthentication(SkyHookRegistration.getUserName(mContext), REALM);
			isPeriodicEnabled = true;
			mHandler.post(mPeriodicUpdates);
			isEnabled = true;
		}
	}
	
	/**
	 * @return true is skyhook is enabled
	 * @author ricky barrette
	 */
	public boolean isEnabled(){
		return isEnabled;
	}
	
	/**
     * Removes any current registration for location updates of the current activity
     * with the given LocationListener.  Following this call, updates will no longer
     * occur for this listener.
	 * @param listener
	 * @author ricky barrette
     */
    public void removeUpdates() {
    	Log.d(TAG,"removeUpdates()");
    	mHandler.removeCallbacks(mFallBack);
    	mListener = null;
    	isPeriodicEnabled = false;
    	if(mXps != null)
    		mXps.abort();
    	if(mSkyHookFallback != null) {
    		Log.d(TAG,"disabling fallback");
    		mSkyHookFallback.disableLocationUpdates();
    		mSkyHookFallback = null;
    		isEnabled = false;
    	}
    }
    
    /**
	 * Used for receiving notifications from SkyHook when
	 * the location has changed. These methods are called if the
	 * LocationListener has been registered with the location manager service using the method.
	 * @param listener
	 * @author ricky barrette
	 */
	public void setLocationListener(GeoPointLocationListener listener){
		Log.d(TAG,"setLocationListener()");
		if (mListener == null) {
			mListener = listener;
		}
	}
    
    private void setUIHandler() {
        mHandler = new Handler() {

			@Override
            public void handleMessage(final Message msg) {
                switch (msg.what) {
	                case LOCATION_MESSAGE:
	                    if (msg.obj instanceof WPSLocation) {
	                    	WPSLocation location = (WPSLocation) msg.obj;
	                    	if (mListener != null && location != null) {
	                    		
	                    		if(Debug.DEBUG)
	                    			Log.d(TAG,"got location "+ location.getLatitude() +", "+ location.getLongitude()+" +- "+ location.getHPE() +"m");
	                    		
	                			mListener.onLocationChanged(new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6)), location.getHPE());
	                			hasLocation = true;
	                		}
						}
						return;
						
	                case ERROR_MESSAGE:
	                    if( msg.obj instanceof WPSReturnCode) {
	                		WPSReturnCode code = (WPSReturnCode) msg.obj;
	                		if ( code != null){
	                			Log.w(TAG, code.toString());
	                		}
	                		hasLocation = false;
	                		
	                		/*
	                		 * check to see if the error returned is an WPS_ERROR_UNAUTHORIZED
	                		 * then check to see if this is the second occurrence of WPS_ERROR_UNAUTHORIZED,
	                		 * if so we will stop skyhook's services to cut down the work load
	                		 */
	                		if(code == WPSReturnCode.WPS_ERROR_UNAUTHORIZED){
	                			if (isUnauthorized){
	                				isPeriodicEnabled = false;
	                				mXps.abort();
//	                				mXps = null;
	                			}
	                			isUnauthorized = true;
	                		}
	                		
	                		
	                		/*
	                		 * check to see if we already have a fall back Scheduled
	                		 * if we dont, and there is not fallback already in place, then schedule one
	                		 */
	                		if((! isFallBackScheduled) && ( mSkyHookFallback == null) && isEnabled) {
	                			Log.d(TAG, "scheduling fallback");
	                			mHandler.postDelayed(mFallBack, mFallBackDelay);
	                			isFallBackScheduled = true;
	                		}
	                	}
	                    return;
	                    
	                case DONE_MESSAGE:
					if (isPeriodicEnabled) {
						mHandler.postDelayed(mPeriodicUpdates, mPeriod);
						Log.d(TAG,"done getting location");
					}
					return;
                }
            }
        };
    }

    /**
     * called from our skyhook to android fall back class
     * (non-Javadoc)
     * @see com.TwentyCodes.android.location.GeoPointLocationListener#onLocationChanged(com.google.android.maps.GeoPoint, int)
     * @author ricky barrette
     */
	@Override
	public void onLocationChanged(GeoPoint point, int accuracy) {
		if(! hasLocation)
			if(mListener != null)
				mListener.onLocationChanged(point, accuracy);
	}

	@Override
	public void onFirstFix(boolean isFistFix) {
		// unused
	}
}    