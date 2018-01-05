package com.example.yan_c_000.auth;

import android.content.Context;
import android.util.Log;

import com.example.yan_c_000.auth.FireDatabase.LatLngMy;
import com.example.yan_c_000.auth.NavigationView.RemoteToLocalCallBack;
import com.example.yan_c_000.auth.Realm.Contacts;
import com.example.yan_c_000.auth.Realm.LocalRealmDB;
import com.example.yan_c_000.auth.Realm.LocationRealm;
import com.example.yan_c_000.auth.RealmChecker.RealmChecker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.RealmResults;

/**
 * Created by yan-c_000 on 22.11.2017.
 */

public class RemoteToLocalLoader {
     private RemoteToLocalCallBack remoteToLocalCallBack;
    private int i;
    public static final String TAG = LocalRealmDB.class.getName();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    Context context;
    private  String myFBuserid;

    interface Callback{
        void RemoteToLocalLoaderCallingBack( );
    }

    private Callback callback;



    public void unregisterCallBack(Callback callback){
        this.callback = null;
    }

    public RemoteToLocalLoader(Context mcontext,String mmyFBuserid) {
        context=mcontext;
        myFBuserid  =mmyFBuserid;
        if (context instanceof Callback) {
            this.callback = (Callback) context;
        }
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("user");

        Load( );

    }

    private   void RemoveUserListener (){
        mFirebaseDatabase.removeEventListener(UserListener);
    }

    private final  ValueEventListener UserListener = new  ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //showData(dataSnapshot);
            Log.e(TAG, "onDataChange"  );
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Log.e(TAG, "CreateContact phone"   + ds.getValue(User.class).getPhone() );

                LocalRealmDB.CreateContact(context, ds.getValue(User.class).getPhone(), ds.getValue(User.class).getName(), ds.getKey());

            }

            RemoveUserListener();
            LoadLocations( );
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    public   void Load (){

        mFirebaseDatabase.addListenerForSingleValueEvent(UserListener);
    }

    public   void CheckMyUser (){
        mFirebaseDatabase = mFirebaseInstance.getReference("user");
        SharedPref2 sharedPref2 = new SharedPref2();
        sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_PHONE);


        String phone = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_PHONE);
        String userId = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID);
        User user = new User(phone, phone );
        mFirebaseDatabase.child(userId).setValue(user);
    }


    private   void LoadLocations (){
        RealmResults<Contacts>   results =  LocalRealmDB.GetAllContacts(context);
         i =results.size();
        String TwoDaysBeforeNow  = String.valueOf(Calendar.getInstance().getTimeInMillis()-172800000);
        String Now  = String.valueOf(Calendar.getInstance().getTimeInMillis());
        ArrayList<String> key = new ArrayList<String>() ;
        if (i>0 ) {


            LoadLocationsForContact( results, i,TwoDaysBeforeNow ,   Now  );
        }


    }





    private   void LoadLocationsForContact (final RealmResults<Contacts> results,final int u,final String TwoDaysBeforeNow ,final String Now ){
        if (u>0) {
            i=u;
            FirebaseDatabase mFirebaseInstance;
            mFirebaseInstance = FirebaseDatabase.getInstance();
            DatabaseReference FireRefLat = mFirebaseInstance.getReference("latlng");
            //FireRefLat.child(contact.getKey()).limitToFirst(10).addValueEventListener(new ValueEventListener() {

            final Contacts contact = results.get(i - 1);

            if (!(contact==null) && !(contact.getKey()==null) && !(contact.getKey().isEmpty())) {
                String Key = contact.getKey();

                FireRefLat.child(Key).orderByKey().startAt(TwoDaysBeforeNow).endAt(Now).limitToLast(7).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        ArrayList<LocationRealm> locations = new ArrayList<LocationRealm>();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //if (!(ds==null) && !(ds.getValue(LatLngMy.class).getLat()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) &&  !(ds.getKey()==null) ) {
                                LocationRealm locationRealm = new LocationRealm();
                                locationRealm.setLat(ds.getValue(LatLngMy.class).getLat());
                                locationRealm.setLon(ds.getValue(LatLngMy.class).getLon());
                                locationRealm.setAccuracy(ds.getValue(LatLngMy.class).getAccuracy());
                                locationRealm.setSpeed(ds.getValue(LatLngMy.class).getSpeed());
                                locationRealm.setFBkey(Long.parseLong(ds.getKey()));
//                                if (ds.getValue(LatLngMy.class).getlastlocaltime() > 0)
//                                    locationRealm.setLocaltimeupdate(ds.getValue(LatLngMy.class).getlastlocaltime());
                                if (ds.getValue(LatLngMy.class).getTimestampLastLong() > 0)
                                    locationRealm.setFBUpdated(ds.getValue(LatLngMy.class).getTimestampLastLong());

                                locationRealm.setFBCreated(ds.getValue(LatLngMy.class).getTimestampCreatedLong());

                                //ds.getValue(ServerValue.TIMESTAMP);
                                locations.add(locationRealm);
                           // }

                        }
//                        if (myFBuserid.equals(contact.getKey()))
//                            RealmChecker.UpdateMyLocations(context, contact, locations);
                        LocalRealmDB.SaveLocations(context, contact, locations);

                        int j = i - 1;
                        LoadLocationsForContact(results, j, TwoDaysBeforeNow, Now);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else {
                int j = i - 1;
                LoadLocationsForContact(results, j, TwoDaysBeforeNow, Now);
            }
        }
        else {
            callback.RemoteToLocalLoaderCallingBack( );

           // Navigation.RedrawContactsMenu(results);
//            for (final Contacts cont  : results) {
//                LocalRealmDB.GetAllLocations(context, cont);
//            }
        }
    }

    private void LoadMyLast5Locations(final RealmResults<Contacts> results, final int u, final String TwoDaysBeforeNow, final String Now) {

        FirebaseDatabase mFirebaseInstance;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference FireRefLat = mFirebaseInstance.getReference("latlng");
        //FireRefLat.child(contact.getKey()).limitToFirst(10).addValueEventListener(new ValueEventListener() {
        SharedPref2 sharedPref2 = new SharedPref2();

        final String userId = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID);


        FireRefLat.child(userId).orderByKey().limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ArrayList<LocationRealm> locations = new ArrayList<LocationRealm>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //if (!(ds==null) && !(ds.getValue(LatLngMy.class).getLat()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) &&  !(ds.getKey()==null) ) {
                    LocationRealm locationRealm = new LocationRealm();

                    locationRealm.setFBkey(Long.parseLong(ds.getKey()));

                    if (ds.getValue(LatLngMy.class).getTimestampLastLong() > 0)
                        locationRealm.setFBUpdated(ds.getValue(LatLngMy.class).getTimestampLastLong());

                    locationRealm.setFBCreated(ds.getValue(LatLngMy.class).getTimestampCreatedLong());

                    //ds.getValue(ServerValue.TIMESTAMP);
                    locations.add(locationRealm);
                    // }

                }
                RealmChecker.UpdateMyLocations(context, locations,userId);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






}
