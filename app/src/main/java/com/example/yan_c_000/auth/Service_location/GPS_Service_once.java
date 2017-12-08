package com.example.yan_c_000.auth.Service_location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.yan_c_000.auth.LatLngMy;
import com.example.yan_c_000.auth.RealmChecker.RealmChecker;
import com.example.yan_c_000.auth.SharedPref2;
import com.example.yan_c_000.auth.UltraHeight;
import com.example.yan_c_000.auth.UltraHeightSingleton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

/**
 * Created by yan-c_000 on 26.09.2017.
 * <p>
 * Created by yan-c_000 on 19.09.2017.
 */

/**
 * Created by yan-c_000 on 19.09.2017.
 */

public class GPS_Service_once extends Service {
    private List<UltraHeight> lastData;
    private UltraHeightSingleton mData;
    private LocationListener listener;
    private LocationManager locationManager;
    public static final String ACTION_ALARM_RECEIVER = "ACTION_ALARM_RECEIVER";
    ///SharedPreferences preferences = getApplicationContext().getSharedPreferences(,Activity.MODE_PRIVATE)  ;
    SharedPref2 sharedPref2 = new SharedPref2();

    String userId = sharedPref2.GetPref (sharedPref2.APP_PREFERENCES_FBID ) ;

     @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    //private FirebaseAuth mAuth;
    //private String userId;

    @Override
    public void onCreate() {
        mData = UltraHeightSingleton.get(this);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("latlng");

        // store app title to 'app_title' node
        // mFirebaseInstance.getReference("app_title").setValue("Realtime Database");


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                i.putExtra("coordinates", location.getLongitude());
                i.putExtra("getLatitude", location.getLatitude());
                sendBroadcast(i);

                //    lastData.add(new UltraHeight(location.getLongitude(), location.getLatitude()));
                double lon = location.getLongitude();
                double lat = location.getLatitude();
                mData.addItem(lon, lat);

                //mAuth = FirebaseAuth.getInstance();
                //FirebaseUser currentUser = mAuth.getCurrentUser();

                //userId = currentUser.getUid();



                //String LocId = mFirebaseDatabase.push().getKey();
                 long LocIdLong=Calendar.getInstance().getTimeInMillis();
                String LocId = String.valueOf(LocIdLong);
                  RealmChecker.CreateLocationChecker (GPS_Service_once.this,LocIdLong, lon, lat ,0,0 );

                LatLngMy latlng = new LatLngMy(  lon, lat ,0,0,0);

                mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);

                //mFirebaseDatabase.child(userId).child(LocId).child("TimeStamp").setValue(ServerValue.TIMESTAMP);
                Log.e(" ", "  " + location.getLongitude() + location.getLatitude());
                stopSelf();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
        // TODO: service can run before permission allowed. Need to check permission granted

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
