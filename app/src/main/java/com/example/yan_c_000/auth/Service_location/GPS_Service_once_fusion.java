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
import com.example.yan_c_000.auth.RealmChecker.LastLocationTimestampChecker;
import com.example.yan_c_000.auth.RealmChecker.LocationRealmChecker;
import com.example.yan_c_000.auth.RealmChecker.RealmChecker;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

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

public class GPS_Service_once_fusion extends Service implements LastLocationTimestampChecker.Callback, GetLastLocationAndOffsetFB.Callback{

    public static final String ACTION_ALARM_RECEIVER = "ACTION_ALARM_RECEIVER";
    ///SharedPreferences preferences = getApplicationContext().getSharedPreferences(,Activity.MODE_PRIVATE)  ;
    SharedPref2 sharedPref2 = new SharedPref2();

    String userId = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID);


    private final static int LOCATION_REQUEST_INTERVAL_SECONDS = 100;
    private final static int MIN_DISPLACEMENT_REQUEST_METRES = 0;
    private final String TAG = this.getClass().getName();
    protected Location mLastLocation;
    boolean waitForCallBackFB = false;
    boolean weFindLocation = false;
    boolean weReturnFb = false;
    long firstTime;
    LocationRealmChecker lastlocation;
    long offset;
    long LocIdLong;

    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mLastLocation = locationResult.getLastLocation();
            weFindLocation = true;
            SendUpdateToFB();
           // SendToFirebase(mLastLocation.getLongitude(),mLastLocation.getLatitude(),mLastLocation.getAccuracy(),mLastLocation.getSpeed());
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
    public void LastLocationTimestampCheckerCallBack( ) {


    }

    @Override
    public void GetLastLocationAndOffsetFB(LocationRealmChecker locationRealm,long moffset , boolean sendfullLtnLng ) {

        offset=moffset;
        lastlocation=locationRealm;
            weReturnFb = true;
        SendUpdateToFB();



    }

    private void SendUpdateToFB (){
        if (weFindLocation){
            if (waitForCallBackFB){
                if (weReturnFb){
                    //SEND FB
                    SenderToFb ();

                }
                else if ((Calendar.getInstance().getTimeInMillis()-firstTime)>40*1000){
                    // нужен таймер на 41 сек для корректной работы
                    //send realm
                    SenderToFb ();
                }

            }
            else {
                //send realm
                SenderToFb ();
            }
        }
    }

    private void SenderToFb() {
          LocIdLong = Calendar.getInstance().getTimeInMillis();
        String LocId = String.valueOf(LocIdLong);
        if (!(lastlocation==null)) {

            Location locationA = new Location("point A");

            locationA.setLatitude(lastlocation.getLat());
            locationA.setLongitude(lastlocation.getLon());

            Location locationB = new Location("point B");

            locationB.setLatitude(mLastLocation.getLatitude());
            locationB.setLongitude(mLastLocation.getLongitude());

            float distance = locationA.distanceTo(locationB);

            double sumaccarcy = lastlocation.getAccuracy() + mLastLocation.getAccuracy();

            if (sumaccarcy < 100) sumaccarcy = 100;

            long acc = (long) sumaccarcy;
//            i.putExtra("distance", "dist"+distance+"Accu"+acc);
//            sendBroadcast(i);
            if (distance > sumaccarcy) {
                // location change send new in Realm & FB
                LatLngMy latlng = new LatLngMy(mLastLocation.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getAccuracy(), mLastLocation.getSpeed(), 0, 0);

                RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, mLastLocation.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getAccuracy(), mLastLocation.getSpeed());


                // LastLocationTimestampChecker last = new LastLocationTimestampChecker(GPS_Service_once_fusion.this,userId, LocId, latlng, lastlocation,false);
                Log.e(TAG, "send class latlng 1");
                mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);

                //  mLastLocationDatabase.child(userId).setValue(latlng);
            } else {
                if (waitForCallBackFB)
                    RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, mLastLocation.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getAccuracy(), mLastLocation.getSpeed());
                else
                    RealmChecker.UpdateLocationChecker(GPS_Service_once_fusion.this, lastlocation, LocIdLong);
                if (!(lastlocation.getFBTimeStamp() > 0)) {
                    LatLngMy latlng = new LatLngMy(mLastLocation.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getAccuracy(), mLastLocation.getSpeed(), 0, offset);
                    mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
//                    mLastLocationDatabase.child(userId).child("lon").setValue(lastlocation.getLon());
//                    mLastLocationDatabase.child(userId).child("lat").setValue(lastlocation.getLat());
//                    mLastLocationDatabase.child(userId).child("accuracy").setValue(lastlocation.getAccuracy());
//                    mLastLocationDatabase.child(userId).child("speed").setValue(lastlocation.getSpeed());
                }
//                Log.e(TAG, "send lastlocaltime");
                mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lastlocaltime").setValue(LocIdLong);
                Log.e(TAG, "send TimeLast");
                mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("timestampCreated").child("TimeLast").setValue(ServerValue.TIMESTAMP);
//                mLastLocationDatabase.child(userId).child("lastlocaltime").setValue(LocIdLong);
//                mLastLocationDatabase.child(userId).child("timestampCreated").child("TimeLast").setValue(ServerValue.TIMESTAMP);
                //LastLocationTimestampChecker last = new LastLocationTimestampChecker(GPS_Service_once_fusion.this,userId, LocId, latlng,lastlocation, true);
            }

        }
        else {
            LatLngMy latlng = new LatLngMy(mLastLocation.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getAccuracy(), mLastLocation.getSpeed(), 0, 0);

            RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, mLastLocation.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getAccuracy(), mLastLocation.getSpeed());


            // LastLocationTimestampChecker last = new LastLocationTimestampChecker(GPS_Service_once_fusion.this,userId, LocId, latlng, lastlocation,false);
            Log.e(TAG, "send class latlng 1");
            mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
        }
        GetTimeStamp();


    }

    private void UpdateRealmAndDestroyService(LocationRealmChecker rlastlocation){
        RealmChecker.UpdateLocationChecker(GPS_Service_once_fusion.this, rlastlocation, LocIdLong);
        stopSelf();
    }



    public  void GetTimeStamp(){
        FirebaseDatabase mFirebaseInstance;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference FireRefLat = mFirebaseInstance.getReference("latlng");
        //FireRefLat.child(contact.getKey()).limitToFirst(10).addValueEventListener(new ValueEventListener() {



        FireRefLat.child(userId).orderByKey().limitToLast(1).addListenerForSingleValueEvent(lastLocationValueEventListener);
    }
    private final ValueEventListener lastLocationValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //ArrayList<LocationRealmChecker> locations = new ArrayList<LocationRealmChecker>();
            if (dataSnapshot.hasChildren()) {

                LocationRealmChecker locationRealm = new LocationRealmChecker();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //if (!(ds==null) && !(ds.getValue(LatLngMy.class).getLat()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) &&  !(ds.getKey()==null) ) {


                    locationRealm.setFBkey(Long.parseLong(ds.getKey()));


                    locationRealm.setFBUpdated(ds.getValue(LatLngMy.class).getTimestampLastLong());

                    locationRealm.setFBTimeStamp(ds.getValue(LatLngMy.class).getTimestampCreatedLong());


                    locationRealm.setLat(ds.getValue(LatLngMy.class).getLat());
                    locationRealm.setLon(ds.getValue(LatLngMy.class).getLon());
                    locationRealm.setAccuracy(ds.getValue(LatLngMy.class).getAccuracy());
                    locationRealm.setSpeed(ds.getValue(LatLngMy.class).getSpeed());
                    locationRealm.setFBkey(Long.parseLong(ds.getKey()));
                    if (ds.getValue(LatLngMy.class).getlastlocaltime() > 0)
                        locationRealm.setTimeLast(ds.getValue(LatLngMy.class).getlastlocaltime());
                    if (ds.getValue(LatLngMy.class).getTimestampLastLong() > 0)
                        locationRealm.setFBUpdated(ds.getValue(LatLngMy.class).getTimestampLastLong());

                    locationRealm.setFBTimeStamp(ds.getValue(LatLngMy.class).getTimestampCreatedLong());
                    UpdateRealmAndDestroyService(locationRealm);
                    //ds.getValue(ServerValue.TIMESTAMP);

                    // }

                }


                //if (!(ds==null) && !(ds.getValue(LatLngMy.class).getLat()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) &&  !(ds.getKey()==null) ) {

                //ds.getValue(ServerValue.TIMESTAMP);
                //locations.add(locationRealm);
                // }


            }
            // FB пуст отправляем реалм c offset
            else  stopSelf();


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


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
    private void WherelastLocation() {
        if (RealmChecker.HaveLastLocation(GPS_Service_once_fusion.this)) {
              lastlocation = RealmChecker.FindLastLocation(GPS_Service_once_fusion.this);
            long timeMinus10minutes = Calendar.getInstance().getTimeInMillis()-10*60*1000;
            if (lastlocation.getTimeLast()>timeMinus10minutes && lastlocation.getTimeLast()>timeMinus10minutes){
                // Реалм свежий, нужно проверить штампы запрашиваем FB и сразу кидаем в FB
                if (lastlocation.getFBUpdated() > 0) {
                    //есть  FBUpdated не запрашиваем FB а сразу кидаем в FB
                    waitForCallBackFB = false;

                } else if (!(lastlocation.getFBTimeStamp() > 0)) {
                    // нет первичного штампа сразу запрашиваем из FB
                    waitForCallBackFB = true;
                    GetLastLocationAndOffsetFB getLastLocationAndOffsetFB = new GetLastLocationAndOffsetFB(GPS_Service_once_fusion.this,userId,lastlocation);


                } else if (lastlocation.getTimeLast() > 0 | !(lastlocation.getFBUpdated() > 0)) {
                    //  есть локальный апдейт 0 и есть первый щтамп, не запрашиваем из FB
                    waitForCallBackFB = false;

                } else
                {
                    // дефолт - запрос из FB
                    waitForCallBackFB = true;
                    GetLastLocationAndOffsetFB getLastLocationAndOffsetFB = new GetLastLocationAndOffsetFB(GPS_Service_once_fusion.this,userId,lastlocation);

                }



            }
            else {
                // реалм старый запрашиваем FB
                waitForCallBackFB = true;

                GetLastLocationAndOffsetFB getLastLocationAndOffsetFB = new GetLastLocationAndOffsetFB(GPS_Service_once_fusion.this,userId,lastlocation);
            }
        }

        else {
            //нету последней в реалме, запрашиваем из FB
            waitForCallBackFB = true;
            GetLastLocationAndOffsetFB getLastLocationAndOffsetFB = new GetLastLocationAndOffsetFB(GPS_Service_once_fusion.this,userId,lastlocation);

        }
    }

    private void initLocationParameters() {
        firstTime=Calendar.getInstance().getTimeInMillis();
        WherelastLocation();




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
        //RemoteToLocalLoader remoteToLocalLoader = new RemoteToLocalLoader(GPS_Service_once_fusion.this,  sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID)  );
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
            LatLngMy latlng = new LatLngMy(  lon, lat, accuracy, speed,0,0);
//            i.putExtra("distance", "dist"+distance+"Accu"+acc);
//            sendBroadcast(i);
            if (distance>sumaccarcy){
                RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, lon, lat, accuracy, speed);


               // LastLocationTimestampChecker last = new LastLocationTimestampChecker(GPS_Service_once_fusion.this,userId, LocId, latlng, lastlocation,false);
                Log.e(TAG, "send class latlng 1"     );
                mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);

                //  mLastLocationDatabase.child(userId).setValue(latlng);
            }
            else{
                RealmChecker.UpdateLocationChecker(GPS_Service_once_fusion.this,lastlocation,LocIdLong);
                if (!(lastlocation.getFBTimeStamp()>0)) {
                    Log.e(TAG, "send lon"     );
                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lon").setValue(lastlocation.getLon());
                    Log.e(TAG, "send lat"     );
                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lat").setValue(lastlocation.getLat());
                    Log.e(TAG, "send accuracy"     );
                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("accuracy").setValue(lastlocation.getAccuracy());
                    Log.e(TAG, "send speed"     );
                    mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("speed").setValue(lastlocation.getSpeed());
//                    mLastLocationDatabase.child(userId).child("lon").setValue(lastlocation.getLon());
//                    mLastLocationDatabase.child(userId).child("lat").setValue(lastlocation.getLat());
//                    mLastLocationDatabase.child(userId).child("accuracy").setValue(lastlocation.getAccuracy());
//                    mLastLocationDatabase.child(userId).child("speed").setValue(lastlocation.getSpeed());
                }
                Log.e(TAG, "send lastlocaltime"     );
                  mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("lastlocaltime").setValue(LocIdLong);
                Log.e(TAG, "send TimeLast"     );
                mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("timestampCreated").child("TimeLast").setValue(ServerValue.TIMESTAMP);
//                mLastLocationDatabase.child(userId).child("lastlocaltime").setValue(LocIdLong);
//                mLastLocationDatabase.child(userId).child("timestampCreated").child("TimeLast").setValue(ServerValue.TIMESTAMP);
                //LastLocationTimestampChecker last = new LastLocationTimestampChecker(GPS_Service_once_fusion.this,userId, LocId, latlng,lastlocation, true);
            }

        }
        else {
            RealmChecker.CreateLocationChecker(GPS_Service_once_fusion.this, LocIdLong, lon, lat, accuracy, speed);

            LatLngMy latlng = new LatLngMy(  lon, lat, accuracy, speed,0,0);
            Log.e(TAG, "send class latlng 2"     );
            mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
//            mLastLocationDatabase.child(userId).setValue(latlng);
//            i.putExtra("FBkey", "null");
//            i.putExtra("distance", "null");
//            sendBroadcast(i);
           // LastLocationTimestampChecker last = new LastLocationTimestampChecker(GPS_Service_once_fusion.this,userId, LocId, latlng,null, false);


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

//    private void SendToFirebaseLatlng(DatabaseReference postRef) {
//        postRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Post p = mutableData.getValue(Post.class);
//                if (p == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                if (p.stars.containsKey(getUid())) {
//                    // Unstar the post and remove self from stars
//                    p.starCount = p.starCount - 1;
//                    p.stars.remove(getUid());
//                } else {
//                    // Star the post and add self to stars
//                    p.starCount = p.starCount + 1;
//                    p.stars.put(getUid(), true);
//                }
//
//                // Set value and report transaction success
//                mutableData.setValue(p);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b,
//                                   DataSnapshot dataSnapshot) {
//                // Transaction completed
//                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
//            }
//        });
//    }



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
