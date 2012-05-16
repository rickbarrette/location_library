/**
 * @author Twenty Codes, LLC
 * @author ricky barrette
 * @date Oct 28, 2010
 */
package com.TwentyCodes.android.location;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.TwentyCodes.android.debug.Debug;
import com.TwentyCodes.android.debug.LocationLibraryConstants;

/**
 * This service class will be used broadcast the users location either one time, or periodically.
 * To use as a one shot location service:
 * <blockquote><pre>PendingIntent pendingIntent = PendingIntent.getService(context, 0, LocationService.startService(context), 0);
 * or
 * Intent service = new Intent(context, LocationService.class);
 * context.startService(service);<pre></bloackquote>
 * To use as a recurring service:
 * <blockquote>LocationService.startService(this, (60000 * Integer.parseInt(ringer.getString(UPDATE_INTVERVAL , "5")))).run();</bloackquote>
 * @author ricky barrette
 */
public class LocationService extends Service implements LocationListener {

	/**
	 * Used to tell the service how frequently it needs to run. This is required if you want a multishot service
	 */
	public static final String INTENT_EXTRA_PERIOD_BETWEEN_UPDATES = "period_beween_updates";
	
	/**
	 * Used to tell the service how accurate of a location you want reported
	 */
	public static final String INTENT_EXTRA_REQUIRED_ACCURACY = "required_accuracy";
	
	/**
	 * Used to tell the service the update action to broadcast. If this is not supplied, {@link BaseLocationReceiver.INTENT_EXTRA_ACTION_UPDATE } will be used.
	 * @see BaseLocationReceiver.INTENT_EXTRA_ACTION_UPDATE
	 */
	public static final String INTENT_EXTRA_ACTION_UPDATE = "action_update";
	
	public static final String TAG = "LocationService";
	private static final int REQUEST_CODE = 7893749;
	private WakeLock mWakeLock;
	private long mPeriod = -1;
	private Location mLocation;
	private int mStartId;
	private AndroidGPS mLocationManager;
	private int mRequiredAccuracy;
	private Intent mIntent;
	/*
	 * this runnable will be qued when the service is created. this will be used as a fail safe
	 */
	private Runnable failSafe = new Runnable() {
		@Override
		public void run(){
			stopSelf(mStartId);
		}
	};

	
	/**
	 * registers this service to be waken up by android's alarm manager
	 * @author ricky barrette
	 */
	private void registerwakeUp(){
		Log.d(TAG, "registerwakeUp()");
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + this.mPeriod, PendingIntent.getService(this, REQUEST_CODE, this.mIntent, 0)); 
	}

	/**
	 * broadcasts location to anything listening for updates,
	 * since this is the last function of the service, we call finish()u
	 * @author ricky barrette
	 */
	private void broadcastLocation() {
		Log.d(TAG, "broadcastLocation()");
		if (mLocation != null) {
			Intent locationUpdate = new Intent();
			if(mIntent.getAction() != null)
				locationUpdate.setAction(mIntent.getAction());
			else
				locationUpdate.setAction(LocationLibraryConstants.INTENT_ACTION_UPDATE);
			locationUpdate.putExtra(LocationManager.KEY_LOCATION_CHANGED, mLocation);
			sendBroadcast(locationUpdate);
			stopSelf(mStartId);
		}
	}

	/**
	 * called when the service is created. this will initialize the location manager, and acquire a wakelock
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 * @author ricky barrette
	 */
	@Override
	public void onCreate(){
		mLocationManager = new AndroidGPS(this);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();
		
		/*
		 * que the fail safe runnable to kill the report location and kill it self after the MAX_RUN_TIME has been meet
		 */
		new Handler().postDelayed(failSafe, LocationLibraryConstants.MAX_LOCATION_SERVICE_RUN_TIME);
		super.onCreate();
	}
	
	/**
	 * called when the service is destroyed. 
	 * this will remove any wakelock or location service running, and register to be waken back up
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 * @author ricky barrette
	 */
	@Override
	public void onDestroy(){
		broadcastLocation();
		mLocationManager.disableLocationUpdates();
		if(mWakeLock.isHeld())
			mWakeLock.release();
		if(mPeriod > -1)
			registerwakeUp();
	}
	
	/**
	 * To keep backwards compatibility we override onStart which is the equivalent of onStartCommand in pre android 2.x
	 * @author ricky barrette
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		if(Debug.DEBUG)
			Log.i(TAG, "onStart.Service started with start id of: " + startId);
		mStartId = startId;
		
		parseIntent(intent);
		
		mLocationManager.enableLocationUpdates(this);
	}
	
	/**
	 * This method is called when startService is called. only used in 2.x android.
	 * @author ricky barrette
	 */
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		if(Debug.DEBUG)
			Log.i(TAG , "onStartCommand.Service started with start id of: " + startId);
        mStartId = startId;
        
        parseIntent(intent);
       
        mLocationManager.enableLocationUpdates(this);
		return START_STICKY;
    }
	
	/**
	 * Parses the incoming intent for the service options
	 * 
	 * @author ricky barrette
	 */
	private void parseIntent(Intent intent){
		
		this.mIntent = intent;
		
		if (intent.hasExtra(INTENT_EXTRA_PERIOD_BETWEEN_UPDATES))
			mPeriod = intent.getLongExtra(INTENT_EXTRA_PERIOD_BETWEEN_UPDATES, 60000L);
		
		if (intent.hasExtra(INTENT_EXTRA_REQUIRED_ACCURACY))
			mRequiredAccuracy = intent.getIntExtra(INTENT_EXTRA_REQUIRED_ACCURACY, -1);
	}
	
	/**
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 * @param arg0
	 * @return
	 * @author ricky barrette
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// UNUSED
		return null;
	}

	/**
	 *a convince method for getting an intent to start the service
	 * @param context
	 * @return a intent that will start the service
	 * @author ricky barrette
	 */
	public static Intent getStartServiceIntent(final Context context){
		return new Intent(context, LocationService.class);
	}
	
	/**
	 * a convince method for stopping the service and removing it's alarm
	 * @param context
	 * @return a runnable that will stop the service
	 * @author ricky barrette
	 */
	public static Runnable stopService(final Context context){
		return new Runnable(){
			@Override
			public void run(){
				context.stopService(new Intent(context, LocationService.class));
	    		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getService(context, REQUEST_CODE, new Intent(context, LocationService.class), 0));
			}
		};
	}

	@Override
	public void onLocationChanged(Location location) {
		if(Debug.DEBUG)
			Log.d(TAG, "got location +- "+ location.getAccuracy() +"m");
		mLocation = location;
		if(location.getAccuracy() <= (mRequiredAccuracy > -1 ? mRequiredAccuracy : LocationLibraryConstants.MINIMUM_REQUIRED_ACCURACY) || LocationLibraryConstants.REPORT_FIRST_LOCATION){
			stopSelf(mStartId);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}