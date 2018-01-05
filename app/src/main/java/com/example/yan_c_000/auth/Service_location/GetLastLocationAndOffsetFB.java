package com.example.yan_c_000.auth.Service_location;

import android.content.Context;

import com.example.yan_c_000.auth.FireDatabase.LatLngMy;
import com.example.yan_c_000.auth.RealmChecker.LocationRealmChecker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by yan-c_000 on 19.12.2017.
 */

public class GetLastLocationAndOffsetFB {


    Context context;

    String userId;

    LocationRealmChecker lastlocationRealm;

    public static final String TAG = GetLastLocationAndOffsetFB.class.getName();
    long offset;
    LocationRealmChecker lastlocation;

    public interface Callback {
        void GetLastLocationAndOffsetFB(LocationRealmChecker locationRealm, long offset, boolean sendfullLtnLng);

    }

    public Callback callback;

    public GetLastLocationAndOffsetFB(Context mcontext, String muserId, LocationRealmChecker mlastlocation) {
        context = mcontext;

        userId = muserId;

        lastlocationRealm = mlastlocation;

        if (context instanceof Callback) {
            this.callback = (Callback) context;
        }
        LoadMyLastLocations();
    }


    public void LoadMyLastLocations() {

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

                    //ds.getValue(ServerValue.TIMESTAMP);

                    // }

                }
                if (locationRealm.getFBkey() > 0 && !(lastlocationRealm==null) && locationRealm.getFBkey() > lastlocationRealm.getFBkey()) {
                    // FB свежее. save to realm checker and send FB location to service
                    callback.GetLastLocationAndOffsetFB(locationRealm, (long) 0, false);

                } else { // FB старее раелм. отправляем целиком реалм в FB с offset
                    LoadOffsetAndLastLocation();
                }

                //if (!(ds==null) && !(ds.getValue(LatLngMy.class).getLat()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) &&  !(ds.getKey()==null) ) {

                //ds.getValue(ServerValue.TIMESTAMP);
                //locations.add(locationRealm);
                // }


            }
            // FB пуст отправляем реалм c offset
            else LoadOffsetAndLastLocation();


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private void LoadOffsetAndLastLocation() {
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");

        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                callback.GetLastLocationAndOffsetFB(lastlocationRealm, offset, true);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }


}

