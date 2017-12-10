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
import com.example.yan_c_000.auth.RealmChecker.LocationRealmChecker;
import com.example.yan_c_000.auth.RealmChecker.RealmChecker;
import com.example.yan_c_000.auth.SharedPref2;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by yan-c_000 on 26.09.2017.
 * <p>
 * Created by yan-c_000 on 19.09.2017.
 */

/**
 * Created by yan-c_000 on 19.09.2017.
 */

public class GPS_Service_once_fusion extends Service {

    public static final String ACTION_ALARM_RECEIVER = "ACTION_ALARM_RECEIVER";
    ///SharedPreferences preferences = getApplicationContext().getSharedPreferences(,Activity.MODE_PRIVATE)  ;
    SharedPref2 sharedPref2 = new SharedPref2();

    String userId = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID);


    private final static int LOCATION_REQUEST_INTERVAL_SECONDS = 10;
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
    //private DatabaseReference mLastLocationDatabase;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    //private FirebaseAuth mAuth;
    //private String userId;

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
        mFirebaseDatabase = mFirebaseInstance.getReference("latlng");
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
        Intent i = new Intent("location_update");
        i.putExtra("coordinates", lon);
        i.putExtra("getLatitude", lat);
        i.putExtra("getAccuracy", (long) accuracy);
        i.putExtra("speed", speed);
        //sendBroadcast(i);
        long LocIdLong = Calendar.getInstance().getTimeInMillis();
        String LocId = String.valueOf(LocIdLong);

        if (RealmChecker.HaveLastLocation(GPS_Service_once_fusion.this)){
            LocationRealmChecker lastlocation = RealmChecker.FindLastLocation (GPS_Service_once_fusion.this);
            Location locationA = new Location("point A");

            locationA.setLatitude(lastlocation.getLat());
            locationA.setLongitude(lastlocation.getLon());

            Location locationB = new Location("point B");

            locationB.setLatitude(lat);
            locationB.setLongitude(lon);

            float distance = locationA.distanceTo(locationB);
            i.putExtra("FBkey", lastlocation.getFBkey());
            double sumaccarcy= lastlocation.getAccuracy() +  accuracy;

            if (sumaccarcy<100) sumaccarcy=100;

            long acc = (long) sumaccarcy;

            i.putExtra("distance", "dist"+distance+"Accu"+acc);
            sendBroadcast(i);
            if (distance>sumaccarcy){
                RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, lon, lat, accuracy, speed);

                LatLngMy latlng = new LatLngMy(  lon, lat, accuracy, speed,0);

                mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
              //  mLastLocationDatabase.child(userId).setValue(latlng);
            }
            else{
                RealmChecker.UpdateLocationChecker(GPS_Service_once_fusion.this,lastlocation,LocIdLong);
                if (!(lastlocation.getFBTimeStamp()>0)) {
                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lon").setValue(lastlocation.getLon());
                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lat").setValue(lastlocation.getLat());
                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("accuracy").setValue(lastlocation.getAccuracy());
                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("speed").setValue(lastlocation.getSpeed());
//                    mLastLocationDatabase.child(userId).child("lon").setValue(lastlocation.getLon());
//                    mLastLocationDatabase.child(userId).child("lat").setValue(lastlocation.getLat());
//                    mLastLocationDatabase.child(userId).child("accuracy").setValue(lastlocation.getAccuracy());
//                    mLastLocationDatabase.child(userId).child("speed").setValue(lastlocation.getSpeed());
                }

                  mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lastlocaltime").setValue(LocIdLong);
                mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("timestampCreated").child("TimeLast").setValue(ServerValue.TIMESTAMP);
//                mLastLocationDatabase.child(userId).child("lastlocaltime").setValue(LocIdLong);
//                mLastLocationDatabase.child(userId).child("timestampCreated").child("TimeLast").setValue(ServerValue.TIMESTAMP);
            }
            stopSelf();
        }
        else {
            RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, lon, lat, accuracy, speed);

            LatLngMy latlng = new LatLngMy(  lon, lat, accuracy, speed,0);

            mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
//            mLastLocationDatabase.child(userId).setValue(latlng);
            i.putExtra("FBkey", "null");
            i.putExtra("distance", "null");
            sendBroadcast(i);
            stopSelf();
        }


//        if (RealmChecker.HaveLastLocation(GPS_Service_once_fusion.this)){
//            LocationRealmChecker lastlocation = RealmChecker.FindLastLocation (GPS_Service_once_fusion.this);
//            Location locationA = new Location("point A");
//
//            locationA.setLatitude(lastlocation.getLat());
//            locationA.setLongitude(lastlocation.getLon());
//
//            Location locationB = new Location("point B");
//
//            locationB.setLatitude(lat);
//            locationB.setLongitude(lon);
//
//            float distance = locationA.distanceTo(locationB);
//            i.putExtra("FBkey", lastlocation.getFBkey());
//            double sumaccarcy= lastlocation.getAccuracy() +  accuracy;
//
//            if (sumaccarcy<100) sumaccarcy=100;
//
//            long acc = (long) sumaccarcy;
//
//            i.putExtra("distance", "dist"+distance+"Accu"+acc);
//            sendBroadcast(i);
//            if (distance>sumaccarcy){
//                RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, lon, lat, accuracy, speed);
//
//                LatLngMy latlng = new LatLngMy(  lon, lat, accuracy, speed,0);
//
//                mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
//
//            }
//            else{
//                RealmChecker.UpdateLocationChecker(GPS_Service_once_fusion.this,lastlocation,LocIdLong);
//                if (!(lastlocation.getFBTimeStamp()>0)) {
//                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lon").setValue(lastlocation.getLon());
//                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lat").setValue(lastlocation.getLat());
//                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("accuracy").setValue(lastlocation.getAccuracy());
//                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("speed").setValue(lastlocation.getSpeed());
//
//                }
//
//                mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lastlocaltime").setValue(LocIdLong);
//                mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("timestampCreated").child("TimeLast").setValue(ServerValue.TIMESTAMP);
//            }
//            stopSelf();
//        }
//        else {
//            RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, lon, lat, accuracy, speed);
//
//            LatLngMy latlng = new LatLngMy(  lon, lat, accuracy, speed,0);
//
//            mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
//            i.putExtra("FBkey", "null");
//            i.putExtra("distance", "null");
//            sendBroadcast(i);
//            stopSelf();
//        }


        //mFirebaseDatabase.child(userId).child(LocId).child("TimeStamp").setValue(ServerValue.TIMESTAMP);
        //Log.e(" ", "  " + location.getLongitude() + location.getLatitude());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
//        if(locationManager != null){
//            //noinspection MissingPermission
//            locationManager.removeUpdates(listener);
//        }
    }


//    @Override
//    public void onCreate() {
//        mData = UltraHeightSingleton.get(this);
//
//        mFirebaseInstance = FirebaseDatabase.getInstance();
//
//        // get reference to 'users' node
//        mFirebaseDatabase = mFirebaseInstance.getReference("latlng");
//
//        // store app title to 'app_title' node
//        // mFirebaseInstance.getReference("app_title").setValue("Realtime Database");
//
//
//        listener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                Intent i = new Intent("location_update");
//                i.putExtra("coordinates", location.getLongitude());
//                i.putExtra("getLatitude", location.getLatitude());
//                sendBroadcast(i);
//
//                //    lastData.add(new UltraHeight(location.getLongitude(), location.getLatitude()));
//                double lon = location.getLongitude();
//                double lat = location.getLatitude();
//                mData.addItem(lon, lat);
//
//                //mAuth = FirebaseAuth.getInstance();
//                //FirebaseUser currentUser = mAuth.getCurrentUser();
//
//                //userId = currentUser.getUid();
//
//
//
//                //String LocId = mFirebaseDatabase.push().getKey();
//                 long LocIdLong=Calendar.getInstance().getTimeInMillis();
//                String LocId = String.valueOf(LocIdLong);
//                  RealmChecker.CreateLocationChecker (GPS_Service_once_fusion.this,LocIdLong, lon, lat  );
//
//                LatLngMy latlng = new LatLngMy(Calendar.getInstance().getTime(), lon, lat );
//
//                mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
//
//                //mFirebaseDatabase.child(userId).child(LocId).child("TimeStamp").setValue(ServerValue.TIMESTAMP);
//                Log.e(" ", "  " + location.getLongitude() + location.getLatitude());
//                stopSelf();
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//            }
//        };
//
//        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//
//        //noinspection MissingPermission
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
//       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
//        // TODO: service can run before permission allowed. Need to check permission granted
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if(locationManager != null){
//            //noinspection MissingPermission
//            locationManager.removeUpdates(listener);
//        }
//    }
}
