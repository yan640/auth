package com.example.yan_c_000.auth.FireDatabase;

import com.example.yan_c_000.auth.LatLngMy;
import com.example.yan_c_000.auth.Realm.LocationRealm;
import com.example.yan_c_000.auth.SharedPref2;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import io.realm.RealmList;

/**
 * Created by yan-c_000 on 04.12.2017.
 */

public class FirebaseSender {


   static public void SendLocationsWithoutTimestamp (RealmList<LocationRealm> realmlocations){
       SharedPref2 sharedPref2 = new SharedPref2();

       String userId = sharedPref2.GetPref(sharedPref2.APP_PREFERENCES_FBID);
        DatabaseReference mFirebaseDatabase;
        FirebaseDatabase mFirebaseInstance;
       mFirebaseInstance = FirebaseDatabase.getInstance();
       mFirebaseDatabase = mFirebaseInstance.getReference("latlng");

       for (LocationRealm loc : realmlocations){
           if (!(loc.getFBCreated()>0)) {
               LatLngMy latlng = new LatLngMy(loc.FBkey, loc.getLon(), loc.getLat(), loc.getAccuracy(), loc.getSpeed(), loc.getLocaltimeupdate());

               mFirebaseDatabase.child(userId).child(String.valueOf(loc.getFBkey())).setValue(latlng);
           }
       }



   }

}
