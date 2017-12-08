package com.example.yan_c_000.auth.Realm;

import android.location.Location;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by yan-c_000 on 15.11.2017.
 */

public class LocationRealm extends RealmObject {

    public Date time;
    public Date timeLast;
    public long FBkey;

    public double lon;
    public double lat;
    public double accuracy;
    public double speed;
    public long FBCreated;
    public long FBUpdated;



    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }



    public long getFBUpdated() {
        return FBUpdated;
    }

    public void setFBUpdated(long FBUpdated) {
        this.FBUpdated = FBUpdated;
    }

    public long getLocaltimeupdate() {
        return localtimeupdate;
    }

    public void setLocaltimeupdate(long localtimeupdate) {
        this.localtimeupdate = localtimeupdate;
    }

    public long localtimeupdate;
    public long getFBCreated() {
        return FBCreated;
    }

    public void setFBCreated(long FBCreated) {
        this.FBCreated = FBCreated;
    }
//    public LocationRealm(Date time, double lon, double lat) {
//        this.time = time;
//        this.lon = lon;
//        this.lat = lat;
//    }

    public Date getTimeLast() {
        return timeLast;
    }

    public void setTimeLast(Date timeLast) {
        this.timeLast = timeLast;
    }

    public long getFBkey() {
        return FBkey;
    }

    public void setFBkey(long FBkey) {
        this.FBkey = FBkey;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
