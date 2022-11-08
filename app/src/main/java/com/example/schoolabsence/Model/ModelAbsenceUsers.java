package com.example.schoolabsence.Model;

public class ModelAbsenceUsers {
    private double latitude;
    private double longitude;
    private String distance;
    private String day;
    private String timeIn;
    private String timeOut;
    private String keyUser;
    private String key;

    public ModelAbsenceUsers(){

    }

    public ModelAbsenceUsers(double latitude, double longitude, String distance, String day, String timeIn, String timeOut, String keyUser) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.day = day;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.keyUser = keyUser;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getKeyUser() {
        return keyUser;
    }

    public void setKeyUser(String keyUser) {
        this.keyUser = keyUser;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
