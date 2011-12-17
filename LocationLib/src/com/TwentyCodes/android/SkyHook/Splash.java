/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Nov 3, 2010
 */
package com.TwentyCodes.android.SkyHook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * this activity will be used to display the Twenty Codes, LLC and Skyhook Wireless Splash Screen
 * @author ricky barrette
 */
public class Splash extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.powered_by_skyhook);
		
		new Handler().postDelayed( new Runnable() { 
			@Override
			public void run(){
				finish();
			}
		} , 1500L);
		
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * a convince method for starting the splash screen activity
	 * @param context
	 * @return a runnable that will start the splash screen
	 * @author ricky barrette
	 */
	public static Runnable showSpashScreen(final Context context){
		return new Runnable() {
			@Override
			public void run(){
				context.startActivity(new Intent(context, com.TwentyCodes.android.SkyHook.Splash.class));
			}
		};
	}
}
