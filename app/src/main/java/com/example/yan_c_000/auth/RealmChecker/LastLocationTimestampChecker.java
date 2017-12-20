package com.example.yan_c_000.auth.RealmChecker;

import android.content.Context;
import android.util.Log;

import com.example.yan_c_000.auth.FireDatabase.LatLngMy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by yan-c_000 on 18.12.2017.
 */

public class LastLocationTimestampChecker {

    Context context;
    final boolean updatelocation;
    String userId;
    String LocId;
    int numberChecks;
    LatLngMy  latlng;
    public static final String TAG = LastLocationTimestampChecker.class.getName();
      long offset;
    LocationRealmChecker lastlocation;
    public interface Callback{
        void LastLocationTimestampCheckerCallBack( );

    }

    public Callback callback;
    public   LastLocationTimestampChecker(Context mcontext,String muserId, String mLocId,LatLngMy mlatlng ,LocationRealmChecker mlastlocation,final boolean mupdatelocation ){
        context=mcontext;
        updatelocation = mupdatelocation;
        userId = muserId;
        LocId =mLocId  ;
        numberChecks=0;
         latlng =mlatlng;
          lastlocation=  mlastlocation;
        if (context instanceof  Callback) {
            this.callback = (  Callback) context;
        }
        LoadMyLastLocations();
    }




    public void LoadMyLastLocations(  ) {

        FirebaseDatabase mFirebaseInstance;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference FireRefLat = mFirebaseInstance.getReference("latlng");
        //FireRefLat.child(contact.getKey()).limitToFirst(10).addValueEventListener(new ValueEventListener() {



        FireRefLat.child(userId).child(LocId).addListenerForSingleValueEvent(lastLocationValueEventListener);
        numberChecks++;
    }


    private final ValueEventListener lastLocationValueEventListener = new ValueEventListener () {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //ArrayList<LocationRealmChecker> locations = new ArrayList<LocationRealmChecker>();
            if (dataSnapshot.hasChildren()){

                    //if (!(ds==null) && !(ds.getValue(LatLngMy.class).getLat()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) && !(ds.getKey()==null) &&  !(ds.getKey()==null) ) {
                    LocationRealmChecker locationRealm = new LocationRealmChecker();

                    locationRealm.setFBkey(Long.parseLong(dataSnapshot.getKey()));

                    if (updatelocation)
                        locationRealm.setFBUpdated(dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong());

                    locationRealm.setFBTimeStamp(dataSnapshot.getValue(LatLngMy.class).getTimestampCreatedLong());

                    //ds.getValue(ServerValue.TIMESTAMP);
                    //locations.add(locationRealm);
                    // }


                RealmChecker.UpdateMyLocation (context, locationRealm, userId);
                callback.LastLocationTimestampCheckerCallBack(  );

            }
            else{
                if (numberChecks<2) LoadMyLastLocations();
                if (numberChecks==2) {
                    Log.e(TAG, "numberChecks=2, but dataSnapshot" +dataSnapshot.toString()  );
                    LoadOffsetAndLastLocation ( );

                }
            }



        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private void LoadOffsetAndLastLocation () {
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");

        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long  offset = snapshot.getValue(Long.class);
                SetOffset(offset);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }
    private void SetOffset (Long moffset) {
        offset=moffset;
        //SendLocation ( );
    }

    private void SendLocation ( ) {
          DatabaseReference mFirebaseDatabase;
          FirebaseDatabase mFirebaseInstance;
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("latlng");
        if ( lastlocation==null  ){

            mFirebaseDatabase.child(userId).child(LocId).setValue(latlng);
        }
        else {

            LatLngMy latlng2 = new LatLngMy(  lastlocation.getLon(), lastlocation.getLat(), lastlocation.getAccuracy(), lastlocation.getSpeed(),lastlocation.getTimeLast(),lastlocation.getFBkey()+offset);
            mFirebaseDatabase.child(userId).child(LocId).setValue(latlng2);
           // mFirebaseDatabase.child(userId).child(String.valueOf(lastlocation.FBkey)).child("timestampCreated").child("TimeLast").setValue(lastlocation.getTimeLast()+offset);
        }

        callback.LastLocationTimestampCheckerCallBack(  );
    }

}
