/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Oct 26, 2010
 */
package com.TwentyCodes.android.SkyHook;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.TwentyCodes.android.debug.Debug;
import com.skyhookwireless.wps.RegistrationCallback;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.XPS;

/**
 * this class will be used to register new users with skyhook
 * @author ricky barrette
 */
public class SkyHookRegistration{
	
	private XPS mXps;
	private Context mContext;

	public SkyHookRegistration(Context context){
		mContext = context;
		mXps = new XPS(context);
	}

	/**
	 * attempts to register the user by their cell #
	 * 
	 * TODO hash cell number for privacy 
	 * @param listener for call back methods
	 * @author ricky barrette
	 */
	public void registerNewUser(RegistrationCallback listener){
		if(mXps != null){
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			if(tm == null)
				Log.v(SkyHook.TAG, "TelephonyManager is null");
			String newUser = tm.getLine1Number();
			
			if(Debug.DEBUG)
				Log.v(SkyHook.TAG, "newUser = " + newUser);
			
			if(newUser == null) {
				Log.e(SkyHook.TAG,"users number is null");
			}
			mXps.registerUser(new WPSAuthentication(SkyHook.USERNAME, SkyHook.REALM), new WPSAuthentication(newUser, SkyHook.REALM), listener);
		}
	}
	
	/**
	 * returns the users username
	 * @param context
	 * @return
	 * @author ricky barrette
	 */
	public static String getUserName(Context context){
		
		switch(Debug.DEFAULT_REGISTRATION_BEHAVIOR){
			case NORMAL:
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				if(tm == null)
					Log.v(SkyHook.TAG, "TelephonyManager is null");
				return tm.getLine1Number();
				
			case RETURN_NULL:
				return null;
				
			case USE_TESTING_USERNAME:
				return SkyHook.USERNAME_FOR_TESTING;
		}
		
		return null;
	}
}