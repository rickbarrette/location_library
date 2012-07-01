/**
 * @author Twenty Codes, LLC
 * @author Ricky Barrette barrette
 * @date Oct 6, 2010
 */
package com.TwentyCodes.android.SkyHook;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.TwentyCodes.android.debug.LocationLibraryConstants;
import com.TwentyCodes.android.location.GeoPointLocationListener;
import com.google.android.maps.GeoPoint;
import com.skyhookwireless.wps.RegistrationCallback;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSReturnCode;

/**
 * This service class will be used broadcast the users location either one time, or periodically.
 * To use as a one shot location service:
 * <blockquote><pre>PendingIntent pendingIntent = PendingIntent.getService(context, 0, SkyHookService.startService(context), 0);
 * or
 * Intent service = new Intent(context, SkyHookService.class);
 * context.startService(service);<pre></bloackquote>
 * To use as a recurring service:
 * <blockquote>SkyHookService.startService(this, (60000 * Integer.parseInt(ringer.getString(UPDATE_INTVERVAL , "5")))).run();</bloackquote>
 * @author ricky barrette
 */
public class SkyHookService extends Service implements GeoPointLocationListener, RegistrationCallback{

	public static final String TAG = "SkyHookService";
	public static final int REQUEST_CODE = 32741942;
	/**
	 * a convince method for getting an intent to start the service
	 * @param context
	 * @return a intent that will be used to start the service
	 * @author ricky barrette
	 */
	public static Intent getStartServiceIntent(final Context context){
		return new Intent(context, SkyHookService.class);
	}
	/**
	 * a convince method for stopping the service and removing its que from the alarm manager
	 * @param context
	 * @return a runnable that will stop the service
	 * @author ricky barrette
	 */
	public static Runnable stopService(final Context context){
		return new Runnable(){
			@Override
			public void run(){
				context.stopService(new Intent(context, SkyHookService.class));
				((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getService(context, REQUEST_CODE, new Intent(context, SkyHookService.class), 0));
			}
		};
	}
	private SkyHook mSkyhook;
	protected long mPeriod = -1;
	private GeoPoint mLocation;
	private int mStartID;
	private int mRequiredAccuracy;

	private Intent mIntent;

	private int mAccuracy;

	/**
	 * broadcasts location to anything listening for updates
	 * 
	 * @author ricky barrette
	 */
	private void braodcastLocation() {
		if (mLocation != null) {
			final Intent locationUpdate = new Intent();
			if(mIntent.getAction() != null)
				locationUpdate.setAction(mIntent.getAction());
			else
				locationUpdate.setAction(LocationLibraryConstants.INTENT_ACTION_UPDATE);
			locationUpdate.putExtra(LocationManager.KEY_LOCATION_CHANGED, convertLocation());
			sendBroadcast(locationUpdate);
		}
	}

	/**
	 * converts skyhook's location object into android's location object
	 * @return converted location
	 * @author ricky barrette
	 */
	public Location convertLocation(){
		final Location location = new Location("location");
		location.setLatitude(mLocation.getLatitudeE6() /1e6);
		location.setLongitude(mLocation.getLongitudeE6() /1e6);
		location.setAccuracy(mAccuracy);
		return location;
	}

	@Override
	public void done() {
		// unused

	}

	/*
	 * I believe that this method is no longer needed as we are not supporting pre 2.1
	 */
	//	/**
	//	 * To keep backwards compatibility we override onStart which is the equivalent of onStartCommand in pre android 2.x
	//	 * @author ricky barrette
	//	 */
	//	@Override
	//	public void onStart(Intent intent, int startId) {
	//		Log.i(SkyHook.TAG, "onStart.Service started with start id of: " + startId);
	//		parseIntent(intent);
	//		this.mSkyhook.getUpdates();
	//	}

	@Override
	public WPSContinuation handleError(final WPSReturnCode arg0) {
		// unused
		return null;
	}

	@Override
	public void handleSuccess() {
		// unused

	}

	/**
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 * @param arg0
	 * @return
	 * @author Ricky Barrette barrette
	 */
	@Override
	public IBinder onBind(final Intent arg0) {
		return null;
	}

	@Override
	public void onCreate(){
		super.onCreate();
		mSkyhook = new SkyHook(this);
		mSkyhook.setLocationListener(this);

		/*
		 * fail safe
		 * this will stop the service after the maximum running time, if location has not been reported
		 */
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				stopSelfResult(mStartID);
			}
		}, LocationLibraryConstants.MAX_LOCATION_SERVICE_RUN_TIME);
	}

	/**
	 * aborts location services
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 * @author Ricky Barrette
	 */
	@Override
	public void onDestroy(){
		mSkyhook.removeUpdates();
		braodcastLocation();
		//ask android to restart service if mPeriod is set
		if(mPeriod > -1)
			registerWakeUp();
		super.onDestroy();
	}

	@Override
	public void onFirstFix(final boolean isFistFix) {
		// unused

	}

	@Override
	public void onLocationChanged(final GeoPoint point, final int accuracy) {
		mLocation = point;
		mAccuracy = accuracy;
		/*
		 * fail safe
		 * if the accuracy is greater than the minimum required accuracy
		 * then continue
		 * else stop to report location
		 */
		if(accuracy < (mRequiredAccuracy > -1 ? mRequiredAccuracy : LocationLibraryConstants.MINIMUM_REQUIRED_ACCURACY) || LocationLibraryConstants.REPORT_FIRST_LOCATION)
			this.stopSelf(mStartID);

	}

	/**
	 * This method is called when startService is called. only used in 2.x android.
	 * @author ricky barrette
	 */
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		Log.i(SkyHook.TAG , "onStartCommand.Service started with start id of: " + startId);
		mStartID = startId;
		parseIntent(intent);
		mSkyhook.getUpdates();
		return START_STICKY;
	}

	/**
	 * Parses the incoming intent for the service options
	 * 
	 * @author ricky barrette
	 */
	private void parseIntent(final Intent intent){

		mIntent = intent;

		if(intent != null){
			if (intent.hasExtra(LocationLibraryConstants.INTENT_EXTRA_PERIOD_BETWEEN_UPDATES))
				mPeriod = intent.getLongExtra(LocationLibraryConstants.INTENT_EXTRA_PERIOD_BETWEEN_UPDATES, LocationLibraryConstants.FAIL_SAFE_UPDATE_INVERVAL);

			if (intent.hasExtra(LocationLibraryConstants.INTENT_EXTRA_REQUIRED_ACCURACY))
				mRequiredAccuracy = intent.getIntExtra(LocationLibraryConstants.INTENT_EXTRA_REQUIRED_ACCURACY, LocationLibraryConstants.MINIMUM_REQUIRED_ACCURACY);
		}
	}

	/**
	 * registers our Receiver the starts the service with the alarm manager
	 * @author ricky barrette
	 */
	private void registerWakeUp(){
		final AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + mPeriod, PendingIntent.getService(this, REQUEST_CODE, mIntent, 0));
	}
}