package com.example.schoolabsence.Model;

public class ModelAbsenceUsers {
    private double latitudeIn;
    private double longitudeIn;
    private double latitudeOut;
    private double longitudeOut;
    private String distanceIn;
    private String distanceOut;
    private String day;
    private String timeIn;
    private String timeOut;
    private String keyUser;
    private String key;

    public ModelAbsenceUsers(){

    }

    public ModelAbsenceUsers(double latitudeIn, double longitudeIn, double latitudeOut, double longitudeOut, String distanceIn, String distanceOut, String day, String timeIn, String timeOut, String keyUser) {
        this.latitudeIn = latitudeIn;
        this.longitudeIn = longitudeIn;
        this.latitudeOut = latitudeOut;
        this.longitudeOut = longitudeOut;
        this.distanceIn = distanceIn;
        this.distanceOut = distanceOut;
        this.day = day;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.keyUser = keyUser;
    }

    public double getLatitudeIn() {
        return latitudeIn;
    }

    public void setLatitudeIn(double latitudeIn) {
        this.latitudeIn = latitudeIn;
    }

    public double getLongitudeIn() {
        return longitudeIn;
    }

    public void setLongitudeIn(double longitudeIn) {
        this.longitudeIn = longitudeIn;
    }

    public double getLatitudeOut() {
        return latitudeOut;
    }

    public void setLatitudeOut(double latitudeOut) {
        this.latitudeOut = latitudeOut;
    }

    public double getLongitudeOut() {
        return longitudeOut;
    }

    public void setLongitudeOut(double longitudeOut) {
        this.longitudeOut = longitudeOut;
    }

    public String getDistanceIn() {
        return distanceIn;
    }

    public void setDistanceIn(String distanceIn) {
        this.distanceIn = distanceIn;
    }

    public String getDistanceOut() {
        return distanceOut;
    }

    public void setDistanceOut(String distanceOut) {
        this.distanceOut = distanceOut;
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
