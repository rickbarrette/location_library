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
	 * @param address to search
	 * @return JSON Array of google place marks
	 * @throws IOException
	 * @throws JSONException
	 * @author ricky barrette
	 */
	public static JSONArray addressSearch(final String address) throws IOException, JSONException {
		String urlStr = "http://maps.google.com/maps/geo?q=" + address + "&output=json&sensor=false";
		urlStr = urlStr.replace(' ', '+');
		final StringBuffer response = new StringBuffer();
		final HttpClient client = new DefaultHttpClient();

		if(Debug.DEBUG)
			Log.d(TAG, urlStr);
		final HttpResponse hr = client.execute(new HttpGet(urlStr));
		final HttpEntity entity = hr.getEntity();

		final BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

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
	public static String getAddressFromLocation(final  Location location) {
		final String urlStr = "http://maps.google.com/maps/geo?q=" + location.getLatitude() + "," + location.getLongitude() + "&output=json&sensor=false";
		final StringBuffer response = new StringBuffer();
		final HttpClient client = new DefaultHttpClient();

		if(Debug.DEBUG)
			Log.d(TAG, urlStr);
		try {
			final HttpResponse hr = client.execute(new HttpGet(urlStr));
			final HttpEntity entity = hr.getEntity();

			final BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

			String buff = null;
			while ((buff = br.readLine()) != null)
				response.append(buff);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		if(Debug.DEBUG)
			Log.d(TAG, response.toString());


		JSONArray responseArray = null;
		try {
			responseArray = new JSONObject(response.toString()).getJSONArray("Placemark");
		} catch (final JSONException e) {
			return location.getLatitude() +", "+ location.getLongitude() +" +/- "+ location.getAccuracy()+"m";
		}

		if(Debug.DEBUG)
			Log.d(TAG,responseArray.length() + " result(s)");

		try {
			final JSONObject jsl = responseArray.getJSONObject(0);
			return jsl.getString("address");
		} catch (final JSONException e) {
			e.printStackTrace();
		}

		return location.getLatitude() +", "+ location.getLongitude() +" +/- "+ location.getAccuracy()+"m";
	}

	/**
	 * Performs a google maps search for the address
	 * @param location
	 * @return JSON Array on google place marks nearby
	 * @author ricky barrette
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONArray getFromLocation(final  Location location) throws IOException, JSONException {
		final String urlStr = "http://maps.google.com/maps/geo?q=" + location.getLatitude() + "," + location.getLongitude() + "&output=json&sensor=false";
		final StringBuffer response = new StringBuffer();
		final HttpClient client = new DefaultHttpClient();

		if(Debug.DEBUG)
			Log.d(TAG, urlStr);
		final HttpResponse hr = client.execute(new HttpGet(urlStr));
		final HttpEntity entity = hr.getEntity();

		final BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

		String buff = null;
		while ((buff = br.readLine()) != null)
			response.append(buff);

		if(Debug.DEBUG)
			Log.d(TAG, response.toString());

		return new JSONObject(response.toString()).getJSONArray("Placemark");
	}
}