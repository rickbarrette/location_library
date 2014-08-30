/**
 * @author Twenty Codes, LLC
 * @author Google Inc.
 * @author ricky barrette
 * @date Oct 2, 2010
 * 
 * Some Code here is Copyright (C) 2008 Google Inc.
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
 * limitations under the License.
 */
package com.TwentyCodes.android.location;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * This class contains common tools for computing common geological problems
 * 
 * @author ricky barrette
 * @author Google Inc.
 */
public class GeoUtils {

	public static final int EARTH_RADIUS_KM = 6371;
	public static final double MILLION = 1000000;

	/**
	 * computes the bearing of lat2/lon2 in relationship from lat1/lon1 in
	 * degrees East
	 * 
	 * @param lat1
	 *            source lat
	 * @param lon1
	 *            source lon
	 * @param lat2
	 *            destination lat
	 * @param lon2
	 *            destination lon
	 * @return the bearing of lat2/lon2 in relationship from lat1/lon1 in
	 *         degrees East of true north
	 * @author Google Inc.
	 */
	public static double bearing(final double lat1, final double lon1, final double lat2, final double lon2) {
		final double lat1Rad = Math.toRadians(lat1);
		final double lat2Rad = Math.toRadians(lat2);
		final double deltaLonRad = Math.toRadians(lon2 - lon1);
		final double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
		final double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);
		return radToBearing(Math.atan2(y, x));
	}

	/**
	 * computes the bearing of lat2/lon2 in relationship from lat1/lon1 in
	 * degrees East of true north
	 * 
	 * @param p1
	 *            source LatLng
	 * @param p2
	 *            destination LatLng
	 * @return the bearing of p2 in relationship from p1 in degrees East
	 * @author Google Inc.
	 */
	public static Double bearing(final LatLng p1, final LatLng p2) {
		return bearing(p1.latitude, p1.longitude, p2.latitude, p2.longitude);
	}

	/**
	 * Calculates the bearing from the user location to the destination
	 * location, or returns the bearing for north if there is no destination.
	 * This method is awesome for making a compass point toward the destination
	 * rather than North.
	 * 
	 * @param user
	 *            location
	 * @param dest
	 *            location
	 * @param bearing
	 *            Degrees East from compass
	 * @return Degrees East of dest location
	 * @author ricky barrette
	 */
	public static float calculateBearing(final LatLng user, final LatLng dest, float bearing) {

		if (user == null || dest == null)
			return bearing;

		final float heading = bearing(user, dest).floatValue();

		bearing = 360 - heading + bearing;

		if (bearing > 360)
			return bearing - 360;

		return bearing;
	}

	/**
	 * Calculates a LatLng x meters away of the LatLng supplied. The new
	 * LatLng shares the same latitude as LatLng point, this way they are on
	 * the same latitude arc.
	 * 
	 * @param point
	 *            central LatLng
	 * @param distance
	 *            in meters from the LatLng
	 * @return LatLng that is x meters away from the LatLng supplied
	 * @author ricky barrette
	 */
	public static LatLng distanceFrom(final LatLng point, double distance) {
		// convert meters into kilometers
		distance = distance / 1000;

		// convert lat and lon of LatLng to radians
		final double lat1Rad = Math.toRadians(point.latitude);
		final double lon1Rad = Math.toRadians(point.longitude);

		/*
		 * kilometers =
		 * acos(sin(lat1Rad)sin(lat2Rad)+cos(lat1Rad)cos(lat2Rad)cos
		 * (lon2Rad-lon1Rad)6371
		 * 
		 * we are solving this equation for lon2Rad
		 * 
		 * lon2Rad =
		 * lon1Rad+acos(cos(meters/6371)sec(lat1Rad)sec(lat2Rad)-tan(lat1Rad
		 * )tan(lat2Rad))
		 * 
		 * NOTE: sec(x) = 1/cos(x)
		 * 
		 * NOTE: that lat2Rad is = lat1Rad because we want to keep the new
		 * LatLng on the same lat arc therefore i saw no need to create a new
		 * variable for lat2Rad, and simply inputed lat1Rad in place of lat2Rad
		 * in the equation
		 * 
		 * NOTE: this equation has be tested in the field against another gps
		 * device, and the distanceKm() from google and has been proven to be
		 * damn close
		 */
		final double lon2Rad = lon1Rad + Math.acos(Math.cos(distance / 6371) * (1 / Math.cos(lat1Rad)) * (1 / Math.cos(lat1Rad)) - Math.tan(lat1Rad) * Math.tan(lat1Rad));

		// return a LatLng that is x meters away from the LatLng supplied
		return new LatLng(point.latitude, (int) (Math.toDegrees(lon2Rad) * 1e6));
	}

	/**
	 * computes the distance between to lat1/lon1 and lat2/lon2 based on the
	 * curve of the earth
	 * 
	 * @param lat1
	 *            source lat
	 * @param lon1
	 *            source lon
	 * @param lat2
	 *            destination lat
	 * @param lon2
	 *            destination lon
	 * @return the distance between to lat1/lon1 and lat2/lon2
	 * @author Google Inc.
	 */
	public static double distanceKm(final double lat1, final double lon1, final double lat2, final double lon2) {
		final double lat1Rad = Math.toRadians(lat1);
		final double lat2Rad = Math.toRadians(lat2);
		final double deltaLonRad = Math.toRadians(lon2 - lon1);
		return Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad)) * EARTH_RADIUS_KM;
	}

	/**
	 * computes the distance between to p1 and p2 based on the curve of the
	 * earth
	 * 
	 * @param p1
	 * @param p2
	 * @return the distance between to p1 and p2
	 * @author Google Inc.
	 */
	public static double distanceKm(final LatLng p1, final LatLng p2) {
		// if we are handed a null, return -1 so we don't break
		if (p1 == null || p2 == null)
			return -1;

		return distanceKm(p1.latitude, p1.longitude, p2.latitude, p2.longitude);
	}

	/**
	 * Converts distance into a human readbale string
	 * 
	 * @param distance
	 *            in kilometers
	 * @param returnMetric
	 *            true if metric, false for US
	 * @return string distance
	 * @author ricky barrette
	 */
	public static String distanceToString(double distance, final boolean returnMetric) {
		final DecimalFormat threeDForm = new DecimalFormat("#.###");
		final DecimalFormat twoDForm = new DecimalFormat("#.##");

		if (returnMetric) {
			if (distance < 1) {
				distance = distance * 1000;
				return twoDForm.format(distance) + " m";
			}
			return threeDForm.format(distance) + " Km";
		}
		distance = distance / 1.609344;
		if (distance < 1) {
			distance = distance * 5280;
			return twoDForm.format(distance) + " ft";
		}
		return twoDForm.format(distance) + " mi";
	}

	/**
	 * a convince method for testing if 2 circles on the the surface of the
	 * earth intersect. we will use this method to test if the users accuracy
	 * circle intersects a marked locaton's radius if ( (accuracyCircleRadius +
	 * locationRadius) - fudgeFactor) >
	 * acos(sin(lat1Rad)sin(lat2Rad)+cos(lat1Rad
	 * )cos(lat2Rad)cos(lon2Rad-lon1Rad)6371
	 * 
	 * @param userPoint
	 * @param accuracyRadius
	 *            in KM
	 * @param locationPoint
	 * @param locationRadius
	 *            in KM
	 * @param fudgeFactor
	 *            how many KM the circles have to intersect
	 * @return true if the circles intersect
	 * @author ricky barrette
	 */
	public static boolean isIntersecting(final LatLng userPoint, final float accuracyRadius, final LatLng locationPoint, final float locationRadius,
			final float fudgeFactor) {
		if (accuracyRadius + locationRadius - fudgeFactor > distanceKm(locationPoint, userPoint))
			return true;
		return false;
	}

//	/**
//	 * determines when the specified point is off the map
//	 *
//	 * @param point
//	 * @return true is the point is off the map
//	 * @author ricky barrette
//	 */
//	public static boolean isPointOffMap(final GoogleMap map, final LatLng point) {
//
//		VisibleRegion vr = map.getProjection().getVisibleRegion();
//		double left = vr.latLngBounds.southwest.longitude;
//		double top = vr.latLngBounds.northeast.latitude;
//		double right = vr.latLngBounds.northeast.longitude;
//		double bottom = vr.latLngBounds.southwest.latitude;
//
//		if (map == null)
//			return false;
//		if (point == null)
//			return false;
//		final LatLng center = map.getCameraPosition().target;
//		final double distance = GeoUtils.distanceKm(center, point);
//		final double distanceLat = GeoUtils.distanceKm(center, new LatLng(center.latitude + map.getLatitudeSpan() / 2, center.longitude));
//		final double distanceLon = GeoUtils.distanceKm(center, new LatLng(center.latitude, center.longitude + map.getLongitudeSpan() / 2));
//		if (distance > distanceLat || distance > distanceLon)
//			return true;
//		return false;
//
//		return map.getProjection().toScreenLocation()
//	}
//
//	/**
//	 * computes a LatLng the is the central LatLng between p1 and p1
//	 *
//	 * @param p1
//	 *            first LatLng
//	 * @param p2
//	 *            second LatLng
//	 * @return a MidPoint object
//	 * @author ricky barrette
//	 */
//	public static MidPoint midpoint(final LatLng p1, final LatLng p2) {
////		double minLatitude = +81 * 1E6;
////		double maxLatitude = -81 * 1E6;
////		double minLongitude = +181 * 1E6;
////		double maxLongitude = -181 * 1E6;
////		final List<Point> mPoints = new ArrayList<Point>();
////		if (p1.latitude != 0 && p1.longitude != 0) {
////			minLatitude = minLatitude > p1.latitude ? p1.latitude : minLatitude;
////			maxLatitude = maxLatitude < p1.latitude ? p1.latitude : maxLatitude;
////			minLongitude = minLongitude > p1.longitude ? p1.longitude : minLongitude;
////			maxLongitude = maxLongitude < p1.longitude ? p1.longitude : maxLongitude;
////			mPoints.add(new Point(p1.latitude, p1.longitude));
////		}
////
////		if (p2.latitude != 0 && p2.longitude != 0) {
////			minLatitude = minLatitude > p2.latitude ? p2.latitude : minLatitude;
////			maxLatitude = maxLatitude < p2.latitude ? p2.latitude : maxLatitude;
////			minLongitude = minLongitude > p2.longitude ? p2.longitude : minLongitude;
////			maxLongitude = maxLongitude < p2.longitude ? p2.longitude : maxLongitude;
////			mPoints.add(new Point(p2.latitude, p2.longitude));
////		}
////		return new MidPoint(new LatLng((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2), minLatitude, minLongitude, maxLatitude, maxLongitude);
//		LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
//		latLngBounds.include(p1);
//		latLngBounds.include(p2);
//		return new MidPoint(latLngBounds.build().getCenter()
//	}

	/**
	 * converts radians to bearing
	 * 
	 * @param rad
	 * @return bearing
	 * @author Google Inc.
	 */
	public static double radToBearing(final double rad) {
		return (Math.toDegrees(rad) + 360) % 360;
	}
}