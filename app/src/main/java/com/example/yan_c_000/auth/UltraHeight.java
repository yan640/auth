package com.example.yan_c_000.auth;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Data of single height measurement
 * Created by Gleb on 13.09.2017.
 */

public class UltraHeight {

    private UUID uuid;

    //высота в мм
    private double height;
    // дата и время измерения
    private double Lat;

    private Date date;

    public UltraHeight(UUID uuid, double height, double Lat, Date date) {
        this.uuid = uuid;
        this.height = height;
        this.Lat = Lat;
        this.date = date;

    }

    public UltraHeight(double height, double Lat, Date date) {
        this(UUID.randomUUID(),height,Lat,date);
    }

    public UltraHeight(double height,double Lat) {
        this(height, Lat, Calendar.getInstance().getTime());
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public double getHeight() {
        return height;
    }
    public double getLat() {
        return Lat;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
