/**
 * LocationService.java
 * @author ricky barrette
 * @date Oct 28, 2010
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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import com.TwentyCodes.android.debug.Debug;
import com.TwentyCodes.android.debug.LocationLibraryConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * This service class will be used broadcast the users location either one time,
 * or periodically.
 * 
 * @author ricky barrette
 */
public class LocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	public static final String TAG = "LocationService";
	private static final int REQUEST_CODE = 7893749;
	private IBinder mBinder;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	// Flag that indicates if a request is underway.
	private boolean mInProgress;
	private Boolean servicesAvailable = false;
	private WakeLock mWakeLock;
	private Location mLocation;
	private int mStartId;
	private int mRequiredAccuracy;
	private Intent mIntent;

	/**
	 * a convince method for getting an intent to start the service
	 *
	 * @param context
	 * @return a intent that will start the service
	 * @author ricky barrette
	 */
	public static Intent getStartServiceIntent(final Context context) {
		return new Intent(context, LocationService.class);
	}

	/**
	 * a convince method for stopping the service and removing it's alarm
	 *
	 * @param context
	 * @return a runnable that will stop the service
	 * @author ricky barrette
	 */
	public static Runnable stopService(final Context context) {
		return new Runnable() {
			@Override
			public void run() {
				context.stopService(new Intent(context, LocationService.class));
				((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getService(context, REQUEST_CODE, new Intent(context,
						LocationService.class), 0));
			}
		};
	}

	/*
	 * this runnable will be qued when the service is created. this will be used
	 * as a fail safe
	 */
	private final Runnable failSafe = new Runnable() {
		@Override
		public void run() {
			stopSelf(mStartId);
		}
	};

	/**
	 * broadcasts location to anything listening for updates, since this is the
	 * last function of the service, we call finish()u
	 * 
	 * @author ricky barrette
	 */
	private void broadcastLocation() {
		if(Debug.DEBUG)
			Log.d(TAG, "broadcastLocation()");
		if (mLocation != null) {
			final Intent locationUpdate = new Intent();
			if (mIntent.getAction() != null)
				locationUpdate.setAction(mIntent.getAction());
			else
				locationUpdate.setAction(LocationLibraryConstants.INTENT_ACTION_UPDATE);
			locationUpdate.putExtra(LocationLibraryConstants.INTENT_EXTRA_LOCATION_CHANGED, mLocation);
			sendBroadcast(locationUpdate);
			stopSelf(mStartId);
		}
	}

	/**
	 * called when the service is created. this will initialize the location
	 * manager, and acquire a wakelock (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 * @author ricky barrette
	 */
	@Override
	public void onCreate() {
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();

		/*
		 * que the fail safe runnable to kill the report location and kill it
		 * self after the MAX_RUN_TIME has been meet
		 */
		new Handler().postDelayed(failSafe, LocationLibraryConstants.MAX_LOCATION_SERVICE_RUN_TIME);
		super.onCreate();

		mInProgress = false;
		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(LocationLibraryConstants.UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(LocationLibraryConstants.FASTEST_INTERVAL);

		servicesAvailable = servicesConnected();

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
		mLocationClient = new LocationClient(this, this, this);
	}

	/**
	 * called when the service is destroyed. this will remove any wakelock or
	 * location service running, and register to be waken back up (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 * @author ricky barrette
	 */
	@Override
	public void onDestroy() {
		broadcastLocation();

		// Turn off the request flag
		mInProgress = false;
		if(servicesAvailable && mLocationClient != null) {
			// Destroy the current location client
			mLocationClient.removeLocationUpdates(this);
			mLocationClient = null;
		}

		if (mWakeLock.isHeld())
			mWakeLock.release();
	}

	@Override
	public void onLocationChanged(final Location location) {
		if (Debug.DEBUG)
			Log.d(TAG, "got location +- " + location.getAccuracy() + "m");
		mLocation = location;
		if (location.getAccuracy() <= (mRequiredAccuracy > -1 ? mRequiredAccuracy : LocationLibraryConstants.MINIMUM_REQUIRED_ACCURACY)
				|| LocationLibraryConstants.REPORT_FIRST_LOCATION)
			stopSelf(mStartId);
	}

	/**
	 * This method is called when startService is called. only used in 2.x
	 * android.
	 * 
	 * @author ricky barrette
	 */
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		if (Debug.DEBUG)
			Log.i(TAG, "onStartCommand.Service started with start id of: " + startId);
		mStartId = startId;

		parseIntent(intent);

		super.onStartCommand(intent, flags, startId);

		if(!servicesAvailable || mLocationClient.isConnected() || mInProgress)
			return START_STICKY;

		setUpLocationClientIfNeeded();
		if(!mLocationClient.isConnected() || !mLocationClient.isConnecting() && !mInProgress)
		{
			mInProgress = true;
			mLocationClient.connect();
		}

		return START_STICKY;
	}

	/**
	 * Parses the incoming intent for the service options
	 * 
	 * @author ricky barrette
	 */
	private void parseIntent(final Intent intent) {
		if (intent == null) {
			this.stopSelf(mStartId);
			Log.e(TAG, "LocationService intent was null, stopping selft: " + mStartId);
		} else {
			mIntent = intent;

			if (intent.hasExtra(LocationLibraryConstants.INTENT_EXTRA_REQUIRED_ACCURACY))
				mRequiredAccuracy = intent.getIntExtra(LocationLibraryConstants.INTENT_EXTRA_REQUIRED_ACCURACY, LocationLibraryConstants.MINIMUM_REQUIRED_ACCURACY);
		}
	}

		public class LocalBinder extends Binder {
			public LocationService getServerInstance() {
				return LocationService.this;
			}
		}

	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {

			return true;
		} else {

			return false;
		}
	}

	/*
     * Create a new location client, using the enclosing class to
     * handle callbacks.
     */
	private void setUpLocationClientIfNeeded()
	{
		if(mLocationClient == null)
			mLocationClient = new LocationClient(this, this, this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Turn off the request flag
		mInProgress = false;
		mLocationClient.removeLocationUpdates(this);
		// Destroy the current location client
		mLocationClient = null;
	}

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		mInProgress = false;

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
		if (connectionResult.hasResolution()) {

			// If no resolution is available, display an error dialog
		} else {

		}
	}
}