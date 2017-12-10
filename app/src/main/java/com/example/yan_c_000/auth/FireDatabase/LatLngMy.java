package com.example.yan_c_000.auth.FireDatabase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by yan-c_000 on 26.10.2017.
 */

/**
 * Created by Ravi Tamada on 07/10/16.
 * www.androidhive.info
 */

@IgnoreExtraProperties
public class LatLngMy {

    //public Date time;
    public long lastlocaltime;



    HashMap<String, Object> timestampCreated;

    public long getlastlocaltime() {
        return lastlocaltime;
    }

    public void setlastlocaltime(long lastloacaltime) {
        lastlocaltime = lastlocaltime;
    }

//    public Date getTime() {
//        return time;
//    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double lon;
    public double lat;
    public double speed;

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

    public double accuracy;
//    public void setTime(Date time) {
//        this.time = time;
//    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public LatLngMy() {
    }

    public LatLngMy(  double lon , double lat,double accuracy,double speed,long lastlocaltime) {
        //this.time = time;
        this.lon = lon;
        this.lat = lat;
        this.accuracy = accuracy;
        this.speed = speed;
        this.lastlocaltime = lastlocaltime;

            HashMap<String, Object> timestampNow = new HashMap<>();
            timestampNow.put("timestamp", ServerValue.TIMESTAMP);
            this.timestampCreated = timestampNow;

    }

    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }


    public  LatLngMy( long FBkey , double lon , double lat,double accuracy,double speed,long lastlocaltime) {
        //this.time = time;
        this.lon = lon;
        this.lat = lat;
        this.accuracy = accuracy;
        this.speed = speed;
        this.lastlocaltime = lastlocaltime;
        if ( lastlocaltime>0) {
            HashMap<String, Object> timestampNow = new HashMap<>();
            timestampNow.put("timestamp", FBkey);
            timestampNow.put("TimeLast", lastlocaltime);
            this.timestampCreated = timestampNow;
        }
        else {
            HashMap<String, Object> timestampNow = new HashMap<>();
            timestampNow.put("timestamp", FBkey);

            this.timestampCreated = timestampNow;
        }
    }


    @Exclude
    public long getTimestampLastLong() {
        if (timestampCreated != null) {
            Object level = timestampCreated.get("TimeLast");
            if (level != null) {
                try {
                    return (long) timestampCreated.get("TimeLast");
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        }
        return 0;
    }

    @Exclude
    public long getTimestampCreatedLong() {
        if (timestampCreated != null) {
            Object level = timestampCreated.get("timestamp");
            if (level != null) {
                try {
                    return (long) timestampCreated.get("timestamp");
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        }
        return 0;
    }
}