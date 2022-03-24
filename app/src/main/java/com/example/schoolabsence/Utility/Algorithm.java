package com.example.schoolabsence.Utility;

import android.util.Log;

public class Algorithm {

    // euclidean distance
    public static double euclidean(double latUserLocation, double longUserLocation, double latSchoolLocation, double longSchoolLocation) {
        double dx = Math.abs(latSchoolLocation - latUserLocation);
        double dy = Math.abs(longSchoolLocation - longUserLocation);
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    // haversine method
    public static double haversine(double latUserLocation, double longUserLocation, double latSchoolLocation, double longSchoolLocation){
        double distance = 0;

        // ---- /_\ lat and long
        double triLat = Math.abs(latSchoolLocation - latUserLocation);
        double triLong = Math.abs(longSchoolLocation - longUserLocation);

        Log.d("tri", triLat +" "+ triLong);
        // a = sin^2(/_\lat / 2) + cos(lat1).cos(lat2).sin^2(/_\long / 2)
        double a = Math.pow(Math.sin(Math.toRadians(triLat / 2)), 2) +
                Math.cos(Math.toRadians(latSchoolLocation)) * Math.cos(Math.toRadians(latUserLocation)) * Math.pow(Math.toRadians(Math.sin(triLong / 2)), 2);
        Log.d("a", String.valueOf(a));

        // arcSin = 2 * a sin sqrt(a)
        double arcSin = 2 * Math.asin(Math.sqrt(a));
        Log.d("arcSin", String.valueOf(arcSin));

        // distance = R * arcSin
        // R = 6371 (KM)
        distance = 6371 * arcSin;
        Log.d("distance", String.valueOf(distance));
        // return distance
        return distance;
    }
}
