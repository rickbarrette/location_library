/**
 * CompassSensor.java
 * @date Mar 2, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.location;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.TwentyCodes.android.debug.Debug;

/**
 * A simple convince class that accesses the compass sensor on another thread
 * @author ricky barrette
 */
public class CompassSensor{
	
	public static final String TAG = "CompassSensor";
	private static final int BEARING = 0;
	private final Display mDisplay;
	private final Handler mHandler;
	private final SensorManager mSensorManager;
	private CompassListener mListener;
	private Context mContext;
	private float mDelination = 0;

	private final SensorEventListener mCallBack = new SensorEventListener() {

		private float[] mRotationMatrix = new float[16];
//		private float[] mRemapedRotationMatrix = new float[16];
		private float[] mI = new float[16];
		private float[] mGravity = new float[3];
		private float[] mGeomag = new float[3];
		private float[] mOrientVals = new float[3];

		private double mAzimuth = 0;
//		double mPitch = 0;
//		double mRoll = 0;
//		private float mInclination;

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
		        boolean success = SensorManager.getRotationMatrix(mRotationMatrix, mI, mGravity, mGeomag);
		        if (success) {
		        	
//		        	switch (mDisplay.getOrientation()){
//			            case Surface.ROTATION_0:
//			            	Log.v(TAG , "0");
//	//		            	SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, mRemapedRotationMatrix);
//			            	break;
//			            case Surface.ROTATION_90:
//			            	Log.v(TAG , "90");
//	//		            	SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, mRemapedRotationMatrix);
//			            	break;
//			            case Surface.ROTATION_180:
//			            	Log.v(TAG , "180");
//	//		            	SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, mRemapedRotationMatrix);
//			            	break;
//			            case Surface.ROTATION_270:
//			            	Log.v(TAG , "270");
//	//		            	SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_Y, mRemapedRotationMatrix);
//			            	break;
//		        	}		        	
		        	
		            /*
		             * remap cords due to Display.getRotation()
		             */
		            SensorManager.getOrientation(mRotationMatrix, mOrientVals);
//		            mInclination = SensorManager.getInclination(mI);
		            mAzimuth = Math.toDegrees(mOrientVals[0]);
//		            mPitch = Math.toDegrees(mOrientVals[1]);
//		            mRoll = Math.toDegrees(mOrientVals[2]);
		            
		            /*
		             * compensate for magentic delination
		             */
		            mAzimuth += mDelination;
		            
		            /*
		             * compensate for device orentation
		             */
		            switch (mDisplay.getOrientation()){
			            case Surface.ROTATION_0:
			            	break;
			            case Surface.ROTATION_90:
			            	mAzimuth = mAzimuth + 90;
			            	break;
			            case Surface.ROTATION_180:
			            	mAzimuth = mAzimuth +180;
			            	break;
			            case Surface.ROTATION_270:
			            	mAzimuth = mAzimuth - 90;
			            	break;
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
		mDisplay = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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