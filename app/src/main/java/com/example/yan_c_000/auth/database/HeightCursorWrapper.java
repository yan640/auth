package com.example.yan_c_000.auth.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.yan_c_000.auth.UltraHeight;
import com.example.yan_c_000.auth.database.DbSchema.HeightsTable.Cols;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Gleb on 14.09.2017.
 */

public class HeightCursorWrapper extends CursorWrapper {

    public HeightCursorWrapper (Cursor cursor) {
        super(cursor);
    }


    public UltraHeight getHeight () {
        String uuidString = getString(getColumnIndex(Cols.UUID));
        double height = getDouble(getColumnIndex(Cols.HEIGHT));
        double Lat = getDouble(getColumnIndex(Cols.LAT));
        long date = getLong(getColumnIndex(Cols.DATE));
        UltraHeight ultraHeight = new UltraHeight(
                UUID.fromString(uuidString),
                height,
                Lat,
                new Date(date)
        );
        return ultraHeight;
    }


}
