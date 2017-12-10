package com.example.yan_c_000.auth.RealmChecker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.yan_c_000.auth.FireDatabase.FirebaseSender;
import com.example.yan_c_000.auth.Realm.Contacts;
import com.example.yan_c_000.auth.Realm.LocalRealmDB;
import com.example.yan_c_000.auth.Realm.LocationRealm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by yan-c_000 on 29.11.2017.
 */

public class RealmChecker extends Activity {
    public static final String TAG = LocalRealmDB.class.getName();

    private Realm realm;

    public static Realm realmInit(Context context){
        Realm realm = null;

        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
        return realm;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // These operations are small enough that
        // we can generally safely run them on the UI thread.

        // Create the Realm instance
        realm = Realm.getDefaultInstance();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    public static RealmResults<LocationRealmChecker> FindLastLocationsChecker(Context context,  long FBkey) {
        Realm realm = realmInit(context);
        RealmResults<LocationRealmChecker> results = realm.where(LocationRealmChecker.class).between("FBkey",FBkey-176400000,FBkey).findAll();

        return results  ;

    }

    public static  boolean    HaveLastLocation (Context context ) {
        Realm realm = realmInit(context);
         Number rf = realm.where(LocationRealmChecker.class).max("FBkey");
        return rf != null;

    }

    public static  LocationRealmChecker  FindLastLocation (Context context ) {
        Realm realm = realmInit(context);
        long sds= (long) realm.where(LocationRealmChecker.class).max("FBkey");

        RealmResults<LocationRealmChecker> results = realm.where(LocationRealmChecker.class).equalTo("FBkey", sds).findAll();

        return results.last()  ;

    }


    public static int FindLocationChecker(Context context, long FBkey) {
        Realm realm = realmInit(context);
        RealmResults<LocationRealmChecker> results = realm.where(LocationRealmChecker.class).equalTo("FBkey", FBkey).findAll();

        return results.size() ;

    }

    public static LocationRealmChecker FindLocationCheckerElement(Context context, long FBkey) {
        Realm realm = realmInit(context);
        RealmResults<LocationRealmChecker> results = realm.where(LocationRealmChecker.class).equalTo("FBkey", FBkey).findAll();

        return results.get(0) ;

    }

    public static void CreateLocationChecker(Context context,  final long FBkey, final double lon,final double lat,final double accuracy,final double speed) {

        Realm realm = realmInit(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a person
                LocationRealmChecker contact = realm.createObject(LocationRealmChecker.class);
                contact.setFBkey(FBkey);
                contact.setLon(lon);
                contact.setLat(lat);
                contact.setAccuracy(accuracy);
                contact.setSpeed(speed);


            }
        });
    }
    public static void UpdateLocationChecker(Context context, final LocationRealmChecker contact,final long TimeLast) {

        Realm realm = realmInit(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                contact.setTimeLast(TimeLast);


            }
        });

    }



    public static void UpdateMyLocations(Context context, final Contacts contact, final ArrayList<LocationRealm> locations ) {
        SimpleDateFormat formating = new SimpleDateFormat("HH:mm:ss.SS");
        Realm realm = realmInit(context);
        long FBkey= Calendar.getInstance().getTimeInMillis();
        final RealmResults<LocationRealmChecker> locationRealms =  FindLastLocationsChecker(context,FBkey);
        //final RealmList<LocationRealm> locationRealms =      GetLocationsAndDelete3dayOld(context, contact);
        for (final LocationRealm lr : locations){
            if (!(lr==null) && !(lr.getFBkey()==0)  ) {
                final RealmResults<LocationRealmChecker> results = realm.where(LocationRealmChecker.class).equalTo("FBkey", lr.getFBkey()).findAll();
                if (results.size()==1){
                    Log.d(TAG, "All good, we find one location  " + lr.getFBkey());
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            results.get(0).setFBTimeStamp(lr.getFBCreated());
                            if (lr.getFBUpdated() > 0)
                                results.get(0).setFBUpdated(lr.getFBUpdated());

                        }
                    });
                }
                else if (results.size()==0) Log.d(TAG, "We didn't find in LocationRealmChecker " + lr.getFBkey());
                else  Log.d(TAG, "We   find in LocationRealmChecker  to  much with FBkey !!!! ;  " + lr.getFBkey());
            }


        }

        RealmResults<LocationRealmChecker> resupdated = FindLastLocationsChecker(  context,    FBkey);
        for (LocationRealmChecker ru : resupdated){
            if (ru.getFBTimeStamp()==0) Log.d(TAG, "We   find location in LocationRealmChecker  without timestamp ;  " +formating.format( ru.getFBkey()));
        }


//        final boolean[] Updated = new boolean[locations.size()];
//        for (int i=0; i<locations.size();i++ ) {
//            final LocationRealm locFB =locations.get(i);
//            for (final LocationRealmChecker locRealm : locationRealms) {
//                if (locFB.getFBkey() == locRealm.getFBkey()) {
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            locRealm.setFBCreated(locFB.getFBCreated());
//                            if (locFB.getFBUpdated() > 0)
//                                locRealm.setFBUpdated(locFB.getFBUpdated());
//
//                        }
//                    });
//                    Updated[i]=true;
//                }
//
//
//            }
//            if (!Updated[i]==true) Updated[i]=false;
//
//
//        }
//
//
//
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                if (locations.size() > 0) {
//                    for (int i=0; i<locations.size();i++ ) {
//                        if (Updated[i]==false){
//                            LocationRealm locFB2 = locations.get(i);
//                            contact.location.add(locFB2);
//                        }
//
//                    }
//                }
//            }
//        });
        FirebaseSender.SendLocationsWithoutTimestamp(contact.getLocation());


//        realm.beginTransaction();
//        realm.insert(locations);
//        realm.commitTransaction();

    }










}
