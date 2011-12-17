/**
 * ReverseGeocoder.java
 * @date Jan 31, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */

package com.TwentyCodes.android.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.TwentyCodes.android.debug.Debug;

/**
 * Due to this bug http://code.google.com/p/android/issues/detail?id=8816 google's Geocoder class does not function in android 2.2+.
 * I found this source in one of the comments mentioning that it is a work around.
 * 
 * @author ricky barrette
 */
public class ReverseGeocoder {

	private static final String TAG = "ReverseGeocoder";

	/**
	 * Performs a google maps search for the address 
	 * @param location
	 * @return JSON Array on google place marks nearby
	 * @author ricky barrette
	 * @throws IOException 
	 * @throws JSONException 
	 */
    public static JSONArray getFromLocation(Location location) throws IOException, JSONException {
    	String urlStr = "http://maps.google.com/maps/geo?q=" + location.getLatitude() + "," + location.getLongitude() + "&output=json&sensor=false";
		StringBuffer response = new StringBuffer();
		HttpClient client = new DefaultHttpClient();
		
		if(Debug.DEBUG)
			Log.d(TAG, urlStr);
		HttpResponse hr = client.execute(new HttpGet(urlStr));
		HttpEntity entity = hr.getEntity();

		BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

		String buff = null;
		while ((buff = br.readLine()) != null)
			response.append(buff);
		
		if(Debug.DEBUG)
			Log.d(TAG, response.toString());
		
		return new JSONObject(response.toString()).getJSONArray("Placemark");
	}
    
    /**
     * Performs a google maps search for the closest address to the location
     * @param lat
     * @param lon
     * @return string address, or lat, lon if search fails
     * @author ricky barrette
     */
    public static String getAddressFromLocation(Location location) {
    	String urlStr = "http://maps.google.com/maps/geo?q=" + location.getLatitude() + "," + location.getLongitude() + "&output=json&sensor=false";
		StringBuffer response = new StringBuffer();
		HttpClient client = new DefaultHttpClient();
		
		if(Debug.DEBUG)
			Log.d(TAG, urlStr);
		try {
			HttpResponse hr = client.execute(new HttpGet(urlStr));
			HttpEntity entity = hr.getEntity();

			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

			String buff = null;
			while ((buff = br.readLine()) != null)
				response.append(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(Debug.DEBUG)
			Log.d(TAG, response.toString());
		

		JSONArray responseArray = null;
		try {
			responseArray = new JSONObject(response.toString()).getJSONArray("Placemark");
		} catch (JSONException e) {
			return location.getLatitude() +", "+ location.getLongitude() +" ± "+ location.getAccuracy()+"m";
		}

		if(Debug.DEBUG)
			Log.d(TAG,responseArray.length() + " result(s)");
		
		try {
			JSONObject jsl = responseArray.getJSONObject(0);
			return jsl.getString("address");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return location.getLatitude() +", "+ location.getLongitude() +" ± "+ location.getAccuracy()+"m";
	}

    /**
     * Performs a google maps search for the address
     * @param address to search
     * @return JSON Array of google place marks
     * @throws IOException
     * @throws JSONException
     * @author ricky barrette
     */
	public static JSONArray addressSearch(String address) throws IOException, JSONException {
		String urlStr = "http://maps.google.com/maps/geo?q=" + address + "&output=json&sensor=false";
		urlStr = urlStr.replace(' ', '+');
		StringBuffer response = new StringBuffer();
		HttpClient client = new DefaultHttpClient();
		
		if(Debug.DEBUG)
			Log.d(TAG, urlStr);
		HttpResponse hr = client.execute(new HttpGet(urlStr));
		HttpEntity entity = hr.getEntity();

		BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

		String buff = null;
		while ((buff = br.readLine()) != null)
			response.append(buff);
		
		if(Debug.DEBUG)
			Log.d(TAG, response.toString());
		
		return new JSONObject(response.toString()).getJSONArray("Placemark");
	}
}