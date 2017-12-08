package com.example.yan_c_000.auth.RealmChecker;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by yan-c_000 on 15.11.2017.
 */

public class LocationRealmChecker extends RealmObject {

    public Date time;
    public long timeLast;

    public long FBkey;
    public long FBTimeStamp;
    public long FBUpdated;

    public double lon;
    public double lat;
    public double accuracy;
    public double speed;



    public long getFBTimeStamp() {
        return FBTimeStamp;
    }

    public void setFBTimeStamp(long FBTimeStamp) {
        this.FBTimeStamp = FBTimeStamp;
    }



    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
    //    public LocationRealm(Date time, double lon, double lat) {
//        this.time = time;
//        this.lon = lon;
//        this.lat = lat;
//    }

    public long getTimeLast() {
        return timeLast;
    }

    public void setTimeLast(long timeLast) {
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
