package com.example.schoolabsence.Model;

public class ModelLocationSchool {
    private String address;
    private double lat_north;
    private double long_north;
    private double lat_south;
    private double long_south;
    private double lat_west;
    private double long_west;
    private double lat_east;
    private double long_east;
    private double median_lat;
    private double median_long;

    public ModelLocationSchool() {
    }

    public ModelLocationSchool(String address, double lat_north, double long_north, double lat_south, double long_south, double lat_west, double long_west, double lat_east, double long_east, double median_lat, double median_long) {
        this.address = address;
        this.lat_north = lat_north;
        this.long_north = long_north;
        this.lat_south = lat_south;
        this.long_south = long_south;
        this.lat_west = lat_west;
        this.long_west = long_west;
        this.lat_east = lat_east;
        this.long_east = long_east;
        this.median_lat = median_lat;
        this.median_long = median_long;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat_north() {
        return lat_north;
    }

    public void setLat_north(double lat_north) {
        this.lat_north = lat_north;
    }

    public double getLong_north() {
        return long_north;
    }

    public void setLong_north(double long_north) {
        this.long_north = long_north;
    }

    public double getLat_south() {
        return lat_south;
    }

    public void setLat_south(double lat_south) {
        this.lat_south = lat_south;
    }

    public double getLong_south() {
        return long_south;
    }

    public void setLong_south(double long_south) {
        this.long_south = long_south;
    }

    public double getLat_west() {
        return lat_west;
    }

    public void setLat_west(double lat_west) {
        this.lat_west = lat_west;
    }

    public double getLong_west() {
        return long_west;
    }

    public void setLong_west(double long_west) {
        this.long_west = long_west;
    }

    public double getLat_east() {
        return lat_east;
    }

    public void setLat_east(double lat_east) {
        this.lat_east = lat_east;
    }

    public double getLong_east() {
        return long_east;
    }

    public void setLong_east(double long_east) {
        this.long_east = long_east;
    }

    public double getMedian_lat() {
        return median_lat;
    }

    public void setMedian_lat(double median_lat) {
        this.median_lat = median_lat;
    }

    public double getMedian_long() {
        return median_long;
    }

    public void setMedian_long(double median_long) {
        this.median_long = median_long;
    }
}
