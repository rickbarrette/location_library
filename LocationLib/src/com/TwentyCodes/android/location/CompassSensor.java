/**
 * CompassSensor.java
 * @date Mar 2, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import com.TwentyCodes.android.debug.Debug;

/**
 * A simple convince class that accesses the compass sensor on another thread
 * @author ricky barrette
 */
public class CompassSensor{
	
	private static final int BEARING = 0;
	private SensorManager mSensorManager;
	private Context mContext;
	private CompassListener mListener;
	private Handler mHandler;
	private SensorCallBack mCallBack;

	/**
	 * A convince callback class for the compass sensor
	 * @author ricky barrette
	 */
	private class SensorCallBack implements SensorEventListener  {

		/**
		 * (non-Javadoc)
		 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
		 * @author ricky barrette
		 */
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// NOT USED
		}

		/**
		 * (non-Javadoc)
		 * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
		 * @author ricky barrette
		 */
		@Override
		public void onSensorChanged(SensorEvent event) {
			float myAzimuth = event.values[0];
			// myPitch = event.values[1];
			float roll = event.values[2];

			if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

				boolean isNormal = false;
				if (roll <= -25)
					isNormal = false;

				if (roll >= 25)
					isNormal = true;

				if (isNormal)
					myAzimuth = myAzimuth + 90;
				else
					myAzimuth = myAzimuth - 90;
			}
			
			mHandler.sendMessage(mHandler.obtainMessage(BEARING, myAzimuth));
		}


    }
	
	/**
	 * Creates a new CompassSensor
	 * @author ricky barrette
	 */
	public CompassSensor(Context context) {
		mContext = context;
		setUiHandler();
		
		//start getting information from the compass sensor
		new Thread(new Runnable(){
			@Override
			public void run() {
				mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);			
			}
		}).start();
		mCallBack = new SensorCallBack();
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
			if(mSensorManager == null)
				mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
			mSensorManager.registerListener(mCallBack, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), Debug.COMPASS_UPDATE_INTERVAL);
		}
	}
	
	/**
	 * Sets up the UI handler
	 * @author ricky barrette
	 */
	private void setUiHandler() {
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
//				System.out.print((Float) msg.obj);
				if(mListener != null)
					if(msg.what == BEARING)
						mListener.onCompassUpdate((Float) msg.obj);
			}
		};
	}

}