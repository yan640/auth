package com.example.yan_c_000.auth.RealmChecker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.yan_c_000.auth.Realm.Contacts;
import com.example.yan_c_000.auth.Realm.LocalRealmDB;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

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

        realm.init(context);
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
         if (rf==null) return false;


        return true  ;

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













}
