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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * This class contains common tools for computing common geological problems
 * @author ricky barrette
 * @author Google Inc.
 */
public class GeoUtils {
    
	public static final int EARTH_RADIUS_KM = 6371;
    public static final double MILLION = 1000000;
    
    /**
     * Calculates the bearing from the user location to the destination location, or returns the bearing for north if there is no destination.
     * This method is awesome for make a compass point toward the destination rather than North.
     * @param user location
     * @param dest location
     * @param bearing Degrees East from compass
     * @return Degrees East of dest location
     * @author ricky barrette
     */
	public static float calculateBearing(GeoPoint user, GeoPoint dest, float bearing) {
		
		if( (user == null) || (dest == null) )
			return bearing;
		
		float heading = bearing(user, dest).floatValue();
		
		bearing =  (360 - heading) + bearing;
		
		if (bearing > 360)
			return bearing - 360;
		
		return bearing;
	}

    /**
     * computes the bearing of lat2/lon2 in relationship from lat1/lon1 in degrees East
     * @param lat1 source lat
     * @param lon1 source lon
     * @param lat2 destination lat
     * @param lon2 destination lon
     * @return the bearing of lat2/lon2 in relationship from lat1/lon1 in degrees East of true north
     * @author Google Inc.
     */
    public static double bearing(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLonRad = Math.toRadians(lon2 - lon1);
        double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);
        return radToBearing(Math.atan2(y, x));
    }
    
    /**
     * computes the bearing of lat2/lon2 in relationship from lat1/lon1 in degrees East of true north
     * @param p1 source geopoint
     * @param p2 destination geopoint
     * @return the bearing of p2 in relationship from p1 in degrees East
     * @author Google Inc.
     */
    public static Double bearing(GeoPoint p1, GeoPoint p2) {
        double lat1 = p1.getLatitudeE6() / MILLION;
        double lon1 = p1.getLongitudeE6() / MILLION;
        double lat2 = p2.getLatitudeE6() / MILLION;
        double lon2 = p2.getLongitudeE6() / MILLION;
        return bearing(lat1, lon1, lat2, lon2);
    }
    
    /**
	 * Calculates a geopoint x meters away of the geopoint supplied. The new geopoint 
	 * shares the same latitude as geopoint point, this way they are on the same latitude arc.
	 * 
	 * @param point central geopoint 
	 * @param distance in meters from the geopoint
	 * @return geopoint that is x meters away from the geopoint supplied
	 * @author ricky barrette
	 */
	public static GeoPoint distanceFrom(GeoPoint point, double distance){
		//convert meters into kilometers
		distance = distance / 1000;
		
		// convert lat and lon of geopoint to radians
		double lat1Rad = Math.toRadians((point.getLatitudeE6() / 1e6));
		double lon1Rad = Math.toRadians((point.getLongitudeE6() / 1e6));		
		
		/*
		 * kilometers = acos(sin(lat1Rad)sin(lat2Rad)+cos(lat1Rad)cos(lat2Rad)cos(lon2Rad-lon1Rad)6371
		 * 
		 * we are solving this equation for lon2Rad
		 *
		 * lon2Rad = lon1Rad+acos(cos(meters/6371)sec(lat1Rad)sec(lat2Rad)-tan(lat1Rad)tan(lat2Rad))
		 * 
		 * NOTE: sec(x) = 1/cos(x)
		 * 
		 * NOTE: that lat2Rad is = lat1Rad because we want to keep the new geopoint on the same lat arc
		 * therefore i saw no need to create a new variable for lat2Rad, 
		 * and simply inputed lat1Rad in place of lat2Rad in the equation
		 * 
		 * NOTE: this equation has be tested in the field against another gps device, and the distanceKm() from google
		 * and has been proven to be damn close
		 */
		double lon2Rad = lon1Rad + Math.acos( Math.cos((distance/6371)) * (1 / Math.cos(lat1Rad)) 
			* (1 / Math.cos(lat1Rad)) - Math.tan(lat1Rad) * Math.tan(lat1Rad));
		
		//return a geopoint that is x meters away from the geopoint supplied
		return new GeoPoint(point.getLatitudeE6(), (int) (Math.toDegrees(lon2Rad) * 1e6));
	}
    
    /**
     * computes the distance between to lat1/lon1 and lat2/lon2 based on the curve of the earth
     * @param lat1 source lat
     * @param lon1 source lon
     * @param lat2 destination lat
     * @param lon2 destination lon
     * @return the distance between to lat1/lon1 and lat2/lon2
     * @author Google Inc.
     */
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLonRad = Math.toRadians(lon2 - lon1);
        return Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad)) * EARTH_RADIUS_KM;
    }
    
    /**
	 * a convince method for testing if 2 circles on the the surface of the earth intersect. 
	 * we will use this method to test if the users accuracy circle intersects a marked locaton's radius 
	 * if ( (accuracyCircleRadius + locationRadius) - fudgeFactor) > acos(sin(lat1Rad)sin(lat2Rad)+cos(lat1Rad)cos(lat2Rad)cos(lon2Rad-lon1Rad)6371
	 * @param userPoint 
	 * @param accuracyRadius in KM
	 * @param locationPoint
	 * @param locationRadius in KM
	 * @param fudgeFactor how many KM the circles have to intersect
	 * @return true if the circles intersect
	 * @author ricky barrette
	 */
	public static boolean isIntersecting(GeoPoint userPoint, float accuracyRadius, GeoPoint locationPoint, float locationRadius, float fudgeFactor){
		if(((accuracyRadius + locationRadius) - fudgeFactor) > distanceKm(locationPoint, userPoint))
			return true;
		return false;
	}
    
    /**
     * computes the distance between to p1 and p2 based on the curve of the earth
     * @param p1
     * @param p2
     * @return the distance between to p1 and p2
     * @author Google Inc.
     */
    public static double distanceKm(GeoPoint p1, GeoPoint p2) {
    	//if we are handed a null, return -1 so we don't break
    	if(p1 == null || p2 == null)
    		return -1;
    	
        double lat1 = p1.getLatitudeE6() / MILLION;
        double lon1 = p1.getLongitudeE6() / MILLION;
        double lat2 = p2.getLatitudeE6() / MILLION;
        double lon2 = p2.getLongitudeE6() / MILLION;
        return distanceKm(lat1, lon1, lat2, lon2);
    }
    
    /**
	 * determines when the specified point is off the map
	 * @param point
	 * @return true is the point is off the map
	 * @author ricky barrette
	 */
	public static boolean isPointOffMap(MapView map , GeoPoint point){
		if(map == null)
			return false;
		if (point == null)
			return false;
		GeoPoint center = map.getMapCenter();
		double distance = GeoUtils.distanceKm(center, point);
		double distanceLat = GeoUtils.distanceKm(center, new GeoPoint((center.getLatitudeE6() + (int) (map.getLatitudeSpan() / 2)), center.getLongitudeE6()));
		double distanceLon = GeoUtils.distanceKm(center, new GeoPoint(center.getLatitudeE6(), (center.getLongitudeE6() + (int) (map.getLongitudeSpan() / 2))));
		if (distance >  distanceLat || distance > distanceLon){
				return true;
		}
		return false;
	}
    
    /**
     * computes a geopoint the is the central geopoint between p1 and p1
     * @param p1 first geopoint
     * @param p2 second geopoint
     * @return a MidPoint object
     * @author ricky barrette
     */
    public static MidPoint midPoint(GeoPoint p1, GeoPoint p2) {
        int minLatitude = (int)(+81 * 1E6);
        int maxLatitude = (int)(-81 * 1E6);
        int minLongitude  = (int)(+181 * 1E6);
        int maxLongitude  = (int)(-181 * 1E6);
	    List<Point> mPoints = new ArrayList<Point>();
	     int latitude = p1.getLatitudeE6();
	     int longitude = p1.getLongitudeE6();
	     if (latitude != 0 && longitude !=0)  {
	          minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
	          maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;      
	          minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
	          maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
	          mPoints.add(new Point(latitude, longitude));
	     }
	     
	     latitude = p2.getLatitudeE6();
	     longitude = p2.getLongitudeE6();
	     if (latitude != 0 && longitude !=0)  {
	         minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
	         maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;      
	         minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
	         maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
	         mPoints.add(new Point(latitude, longitude));
	    }
        return new MidPoint(new GeoPoint((maxLatitude + minLatitude)/2, (maxLongitude + minLongitude)/2 ), minLatitude, minLongitude, maxLatitude, maxLongitude);  
    }
    
    /**
     * converts radians to bearing
     * @param rad
     * @return bearing
     * @author Google Inc.
     */
    public static double radToBearing(double rad) {
        return (Math.toDegrees(rad) + 360) % 360;
    }
}