/**
 * CompassSensor.java
 * @date Mar 2, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.TwentyCodes.android.debug.Debug;

/**
 * A simple convince class that accesses the compass sensor on another thread
 * @author ricky barrette
 */
public class CompassSensor{
	
	private static final int BEARING = 0;
	private final SensorManager mSensorManager;
	private CompassListener mListener;
	private final Handler mHandler;
	private Context mContext;
	private float mDelination = 0;
	public static final String TAG = "CompassSensor";

	private final SensorEventListener mCallBack = new SensorEventListener() {

		private float[] mR = new float[16];
		private float[] mI = new float[16];
		private float[] mGravity = new float[3];
		private float[] mGeomag = new float[3];
		private float[] mOrientVals = new float[3];

		private double mAzimuth = 0;
		double mPitch = 0;
		double mRoll = 0;
		private float mInclination;

		public void onSensorChanged(final SensorEvent sensorEvent) {
			if(Debug.DEBUG){
				switch (sensorEvent.accuracy){
					case SensorManager.SENSOR_STATUS_UNRELIABLE:
						Log.v(TAG , "UNRELIABLE");
						break;
					case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
						Log.v(TAG , "LOW");
						break;
					case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
						Log.v(TAG , "MEDIUM");
						break;
					case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
						Log.v(TAG , "HIGH");
						break;
					
				}
			}

			// If the sensor data is unreliable return
		    if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
		        return;

		    // Gets the value of the sensor that has been changed
		    switch (sensorEvent.sensor.getType()) {  
		        case Sensor.TYPE_ACCELEROMETER:
		            mGravity = sensorEvent.values.clone();
		            break;
		        case Sensor.TYPE_MAGNETIC_FIELD:
		            mGeomag = sensorEvent.values.clone();
		            break;
		    }

		    // If gravity and geomag have values then find rotation matrix
		    if (mGravity != null && mGeomag != null) {

		        // checks that the rotation matrix is found
		        boolean success = SensorManager.getRotationMatrix(mR, mI, mGravity, mGeomag);
		        if (success) {
		        	
		        	/*
		        	 * TODO remap cords due to Display.getRotation()
		        	 */
//		        	Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//		        	switch (display.getOrientation()){
//		        	}
		        	
//		        	SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, mR);
		            SensorManager.getOrientation(mR, mOrientVals);
		            mInclination = SensorManager.getInclination(mI);
		            mAzimuth = Math.toDegrees(mOrientVals[0]);
		            mPitch = Math.toDegrees(mOrientVals[1]);
		            mRoll = Math.toDegrees(mOrientVals[2]);
		            
		            /*
		             * compensate for magentic delination
		             */
		            mAzimuth += mDelination;
		            
		            /*
		             * this will compenstate for device rotations
		             * TODO compensate for inversed portrait
		             */
		            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						boolean isNormal = false;
						if (mRoll <= -25)
							isNormal = false;

						if (mRoll >= 25)
							isNormal = true;

						if (isNormal)
							mAzimuth = mAzimuth - 90;
						else
							mAzimuth = mAzimuth + 90;
					}
		        }
		    }
		    
		    mHandler.sendMessage(mHandler.obtainMessage(BEARING, (float) mAzimuth));
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
    };
	
	/**
	 * Creates a new CompassSensor
	 * @author ricky barrette
	 */
	public CompassSensor(final Context context) {
		mContext = context;
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(mListener != null)
					if(msg.what == BEARING)
						mListener.onCompassUpdate((Float) msg.obj);
			}
		};
		
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}
	
	/**
	 * Disables compass updates
	 * @author ricky barrette
	 */
	public void disable(){
		mListener = null;
		mSensorManager.unregisterListener(mCallBack);
	}

	/**
	 * Attempts to register the listener for compass updates
	 * @param listener
	 * @author ricky barrette
	 */
	public void enable(CompassListener listener){
		if(mListener == null) {
			mListener = listener;
			if(mSensorManager != null)
				new Thread(new Runnable(){
					@Override
					public void run() {
						// Register this class as a listener for the accelerometer sensor
						mSensorManager.registerListener(mCallBack, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), Debug.COMPASS_UPDATE_INTERVAL);
						// ...and the orientation sensor
						mSensorManager.registerListener(mCallBack, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), Debug.COMPASS_UPDATE_INTERVAL);
					}
				}).start();
		}
	}
	
	/**
	 * Updates the Geomagnetic Field Declination based off of the provided location
	 * @param location last known (lat,lon,altitude), null will reset
	 * @author ricky barrette
	 */
	public void setDeclination(final Location location){
        if (location != null) {
            final GeomagneticField geomagneticField = new GeomagneticField(new Double(location.getLatitude()).floatValue(), 
            		new Double(location.getLongitude()).floatValue(), 
                    new Double(location.getAltitude()).floatValue(), 
                    System.currentTimeMillis());
            mDelination = geomagneticField.getDeclination();
        } else {
        	mDelination = 0;
        }
	}
}