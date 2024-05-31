//package com.example.myandroid;
import java.io.*;
public class Waypoint implements Serializable {
    private double lat;
    private double lon;
    private double ele;
    private String time;

    public Waypoint(double lat, double lon, double ele, String time)
    {
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        this.time = time;
    }

    public double getLat()
    {
        return this.lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLon()
    {
        return this.lon;
    }

    public void setLon(double lon)
    {
        this.lon = lon;
    }

    public double getEle()
    {
        return this.ele;
    }

    public void setEle(double ele)
    {
        this.ele = ele;
    }

    public String getTime()
    {
        return this.time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    @Override
    public String toString() {
        return lat + "," + lon + "," + ele + "," + time;
    }
}

