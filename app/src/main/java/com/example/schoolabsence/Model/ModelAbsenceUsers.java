package com.example.schoolabsence.Model;

public class ModelAbsenceUsers {
    private double latitude;
    private double longitude;
    private String distance;
    private String day;
    private String time;
    private String keyUser;
    private String key;

    public ModelAbsenceUsers(){

    }

    public ModelAbsenceUsers(double latitude, double longitude, String distance, String day, String time, String keyUser) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.day = day;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
