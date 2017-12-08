package com.example.yan_c_000.auth;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.yan_c_000.auth.Service_location.GPS_Service_once_fusion;

import java.util.Calendar;

/**
 * Created by yan-c_000 on 08.10.2017.
 */

public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context ctx = getApplicationContext();
/** this gives us the time for the first trigger.  */
        Calendar cal = Calendar.getInstance();
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60 * 2; // 5/10 minutes in milliseconds
//        Intent serviceIntent = new Intent(ctx, GPS_Service_once.class);
//// make sure you **don't** use *PendingIntent.getBroadcast*, it wouldn't work
//        serviceIntent.setAction(GPS_Service_once.ACTION_ALARM_RECEIVER);
        Intent serviceIntent = new Intent(ctx, GPS_Service_once_fusion.class);
// make sure you **don't** use *PendingIntent.getBroadcast*, it wouldn't work
        serviceIntent.setAction(GPS_Service_once_fusion.ACTION_ALARM_RECEIVER);
        PendingIntent servicePendingIntent =
                PendingIntent.getService(ctx,
                        0, // integer constant used to identify the service
                        serviceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running
// there are other options like setInexactRepeating, check the docs
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,//type of alarm. This one will wake up the device when it goes off, but there are others, check the docs
                cal.getTimeInMillis(),
                interval,
                servicePendingIntent);

    }
}