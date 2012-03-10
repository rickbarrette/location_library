/**
 * CompassSensor.java
 * @date Mar 2, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import com.TwentyCodes.android.debug.Debug;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

/**
 * A simple convince class that accesses the compass sensor on another thread
 * @author ricky barrette
 */
public class CompassSensor{
	
	private static final int BEARING = 0;
	private final SensorManager mSensorManager;
	private CompassListener mListener;
	private final Handler mHandler;

	private final SensorEventListener mCallBack = new SensorEventListener() {

		private float[] inR = new float[16];
		private float[] I = new float[16];
		private float[] gravity = new float[3];
		private float[] geomag = new float[3];
		private float[] orientVals = new float[3];

		private double azimuth = 0;
//		double pitch = 0;
//		double roll = 0;

		public void onSensorChanged(SensorEvent sensorEvent) {
		    // If the sensor data is unreliable return
		    if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
		        return;

		    // Gets the value of the sensor that has been changed
		    switch (sensorEvent.sensor.getType()) {  
		        case Sensor.TYPE_ACCELEROMETER:
		            gravity = sensorEvent.values.clone();
		            break;
		        case Sensor.TYPE_MAGNETIC_FIELD:
		            geomag = sensorEvent.values.clone();
		            break;
		    }

		    // If gravity and geomag have values then find rotation matrix
		    if (gravity != null && geomag != null) {

		        // checks that the rotation matrix is found
		        boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
		        if (success) {
		            SensorManager.getOrientation(inR, orientVals);
		            azimuth = Math.toDegrees(orientVals[0]);
//		            pitch = Math.toDegrees(orientVals[1]);
//		            roll = Math.toDegrees(orientVals[2]);
		        }
		    }
		    
		    mHandler.sendMessage(mHandler.obtainMessage(BEARING, (float) azimuth));
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
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
//				System.out.print((Float) msg.obj);
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
}