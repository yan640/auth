package com.example.yan_c_000.auth.Service_location;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.yan_c_000.auth.FireDatabase.LatLngMy;

import com.example.yan_c_000.auth.SharedPref2;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yan-Desktop on 01.01.2018.
 */

public class GPS_Service_once_fusion_last extends Service {
    public static final String ACTION_ALARM_RECEIVER = "ACTION_ALARM_RECEIVER";
     SharedPref2 sharedPref2 = new SharedPref2();

    String userId = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID);


    private final static int LOCATION_REQUEST_INTERVAL_SECONDS = 100;
    private final static int MIN_DISPLACEMENT_REQUEST_METRES = 0;
    private final String TAG = this.getClass().getName();
    protected Location mLastLocation;


    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mLastLocation = locationResult.getLastLocation();
            SendToFirebase(mLastLocation.getLongitude(),mLastLocation.getLatitude(),mLastLocation.getAccuracy(),mLastLocation.getSpeed());
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
     private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;





    @Override
    public void onCreate() {

        initLocationParameters();
    }

    @NonNull
    private FusedLocationProviderClient getFusedLocationProviderClient() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
        return fusedLocationProviderClient;
    }


    private void initLocationParameters() {




        mFirebaseInstance = FirebaseDatabase.getInstance();
        // mFirebaseInstance.setPersistenceEnabled(true);
        mFirebaseDatabase = mFirebaseInstance.getReference("LastLocation");
        // mLastLocationDatabase = mFirebaseInstance.getReference("LastLocation");
        FusedLocationProviderClient locationProviderClient = getFusedLocationProviderClient();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(LOCATION_REQUEST_INTERVAL_SECONDS));
        locationRequest.setSmallestDisplacement(MIN_DISPLACEMENT_REQUEST_METRES);
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
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Log.d(TAG, "Getting location...");


    }






    public void SendToFirebase(double lon, double lat, double accuracy, double speed) {

        long LocIdLong = Calendar.getInstance().getTimeInMillis();
        String LocId = String.valueOf(LocIdLong);


        LatLngMy latlng = new LatLngMy(  lon, lat, accuracy, speed,0,0);

        mFirebaseDatabase.child(userId).setValue(latlng);
        CheckOffset();


    }

//
    private void CheckOffset () {
    DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");

    offsetRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            long  offset = snapshot.getValue(Long.class);
            SetOffset( );
        }

        @Override
        public void onCancelled(DatabaseError error) {
            System.err.println("Listener was cancelled");
            SetOffset( );
        }
    });
    }
    private void SetOffset (   ) {
        stopSelf();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

    }


}
