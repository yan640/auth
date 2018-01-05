package com.example.yan_c_000.auth.FireDatabase;

import android.content.Context;
import android.util.Log;

import com.example.yan_c_000.auth.Realm.Contacts;
import com.example.yan_c_000.auth.Realm.LocalRealmDB;
import com.example.yan_c_000.auth.Realm.LocationRealm;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.RealmResults;

import static com.example.yan_c_000.auth.Realm.LocalRealmDB.GetAllContacts;

/**
 * Created by yan-c_000 on 10.12.2017.
 */

public class FBLocationListener {

    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference mLastLocationDatabase;
    private FirebaseDatabase mFirebaseInstance;
    Context context;
    public static final String TAG = FBLocationListener.class.getName();
    SimpleDateFormat formating = new SimpleDateFormat("MM:dd:HH:mm:ss.SS");
    public interface Callback{
        void FBLocationListenerCallBack(String phone );
        void FBLocationListenerCallBackNewLocation(String phone );
    }

    public  Callback callback;




    public FBLocationListener(Context mcontext ) {
        context=mcontext;
        if (context instanceof  Callback) {
            this.callback = ( Callback) context;
        }

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("latlng");
//        mLastLocationDatabase = mFirebaseInstance.getReference("LastLocation");
        Listen( );

    }

    public void Listen(){
        String now =  String.valueOf(Calendar.getInstance().getTimeInMillis() );


        RealmResults<Contacts> contacts= GetAllContacts (  context);

        for (Contacts cont : contacts){
            if (!(cont.getKey()==null && !(cont.getKey().isEmpty())) ) {
                if (!(cont.location.isEmpty()) && !(cont.location.last()==null) && !(cont.location.last().getFBkey()==0)) {
                    String last = String.valueOf(cont.location.last().getFBkey()-1);
                    Log.e(TAG, "find last FBkey for cont   " +   cont.getKey() );
                    mFirebaseDatabase.child(cont.getKey()).orderByKey().startAt(last).addChildEventListener(LastLocationListener);
                }
                else {
                    Log.e(TAG, "didn't find last FBkey for cont   " +   cont.getKey()     );
                    mFirebaseDatabase.child(cont.getKey()).limitToLast(2).addChildEventListener(LastLocationListener);
                }
                //if (!(cont.location.last()==null) &&  cont.location.last().getFBkey()>0 ) now = cont.location.last().getFBkey();

            }
//            mFirebaseDatabase.child( cont.getKey()).orderByKey().startAt(now) .addValueEventListener(LocationListener);
//            mLastLocationDatabase.child( cont.getKey()).addChildEventListener().addValueEventListener(LastLocationListener);
        }

    }

    public void ResetListener(String userFBkey, Long LatLngFbkey){
        mFirebaseDatabase.child(userFBkey).removeEventListener(LocationListener);
        String now = String.valueOf(LatLngFbkey+1);
        mFirebaseDatabase.child(userFBkey).orderByKey().startAt(now).addValueEventListener(LocationListener);

    }

    public final ChildEventListener LastLocationListener = new  ChildEventListener(){

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //showData(dataSnapshot);
            DatabaseReference ref  = dataSnapshot.getRef();
            String LocationFbkey = ref.getKey() ;
            String UserFBkey = ref.getParent().getKey().toString();
            //Log.e(TAG, "onChildAdded for cont  "+ reff  +" size: "+dataSnapshot.getChildrenCount()  );
            long FBkey=(Calendar.getInstance().getTimeInMillis());

            Log.e(TAG, "ref"+ ref    );
            Log.e(TAG, "onChildAdded    UserFBkey  "+ UserFBkey+" LocationFbkey   "+ LocationFbkey+" size: "+dataSnapshot.toString() );
            try {
                if (!(dataSnapshot.getRef().getKey().isEmpty())
                        && !(dataSnapshot.getRef().getKey() == null)
                        && Long.parseLong(dataSnapshot.getRef().getKey()) > 1012567391
                        && !(UserFBkey == null)

                        ) {

                    LocationRealm locationRealm = new LocationRealm();
                    locationRealm.setLat(dataSnapshot.getValue(LatLngMy.class).getLat());
                    locationRealm.setLon(dataSnapshot.getValue(LatLngMy.class).getLon());
                    locationRealm.setAccuracy(dataSnapshot.getValue(LatLngMy.class).getAccuracy());
                    locationRealm.setSpeed(dataSnapshot.getValue(LatLngMy.class).getSpeed());
                    locationRealm.setFBkey(Long.parseLong(dataSnapshot.getRef().getKey()));
//                    if (dataSnapshot.getValue(LatLngMy.class).getlastlocaltime() > 0)
//                        locationRealm.setLocaltimeupdate(dataSnapshot.getValue(LatLngMy.class).getlastlocaltime());
                    if (dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong() > 0)
                        locationRealm.setFBUpdated(dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong());

                    locationRealm.setFBCreated(dataSnapshot.getValue(LatLngMy.class).getTimestampCreatedLong());
                    long ss = Calendar.getInstance().getTimeInMillis() - locationRealm.getFBCreated();
                    LocalRealmDB.UpdateLastLocationForContact(context, ref.getParent().getKey(), locationRealm);
                    String phone =LocalRealmDB.UpdateLastLocationForContact(context, ref.getParent().getKey(), locationRealm);

                    Log.e(TAG, "onChildAdded     We find  for cont  "+ UserFBkey+" size: "+dataSnapshot.getChildrenCount()+" time:  "+ss );
                    if (!(phone.isEmpty())){
                        callback.FBLocationListenerCallBackNewLocation( phone);
                    }


                }


            } catch (NullPointerException e) {
                Log.e(TAG, "onChildAdded   NullPointerException");
            }





           // if (dataSnapshot.getChildrenCount() >0) ResetListener(dataSnapshot.getRef().getKey(),FBkey);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            //not supportDatabaseReference ref  = dataSnapshot.getRef();
            DatabaseReference ref  = dataSnapshot.getRef();
            String LocationFbkey = ref.getKey() ;
            String UserFBkey = ref.getParent().getKey() ;
            //Log.e(TAG, "onChildAdded for cont  "+ reff  +" size: "+dataSnapshot.getChildrenCount()  );
            long FBkey=(Calendar.getInstance().getTimeInMillis());

            Log.e(TAG, "ref"+ ref    );
            Log.e(TAG, "onChildChanged    UserFBkey  "+ UserFBkey+" LocationFbkey   "+ LocationFbkey+" size: "+dataSnapshot.toString() );
            try {
                if (!(dataSnapshot.getRef().getKey().isEmpty())
                        && !(dataSnapshot.getRef().getKey() == null)
                        && Long.parseLong(dataSnapshot.getRef().getKey()) > 1012567391
                        && !(UserFBkey == null)

                        ) {

                    LocationRealm locationRealm = new LocationRealm();
                    locationRealm.setLat(dataSnapshot.getValue(LatLngMy.class).getLat());
                    locationRealm.setLon(dataSnapshot.getValue(LatLngMy.class).getLon());
                    locationRealm.setAccuracy(dataSnapshot.getValue(LatLngMy.class).getAccuracy());
                    locationRealm.setSpeed(dataSnapshot.getValue(LatLngMy.class).getSpeed());
                    locationRealm.setFBkey(Long.parseLong(dataSnapshot.getRef().getKey()));
//                    if (dataSnapshot.getValue(LatLngMy.class).getlastlocaltime() > 0)
//                        locationRealm.setLocaltimeupdate(dataSnapshot.getValue(LatLngMy.class).getlastlocaltime());
                    if (dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong() > 0)
                        locationRealm.setFBUpdated(dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong());

                    locationRealm.setFBCreated(dataSnapshot.getValue(LatLngMy.class).getTimestampCreatedLong());
                    long ss = Calendar.getInstance().getTimeInMillis() - locationRealm.getFBCreated();
                    String phone =LocalRealmDB.UpdateLastLocationForContact(context, ref.getParent().getKey(), locationRealm);

                    Log.e(TAG, "onChildChanged   We find  for cont  " + UserFBkey + " size: " + dataSnapshot.getChildrenCount() + " time:  " + ss);
                    if (!(phone.isEmpty())){
                        callback.FBLocationListenerCallBack( phone);
                    }

                }


            } catch (NullPointerException e) {
                Log.e(TAG, "onChildChanged   NullPointerException");
            }



        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {


        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            //not support
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //not support
        }
    };

    public final ValueEventListener LocationListener = new  ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //showData(dataSnapshot);
            DatabaseReference ref  = dataSnapshot.getRef();
            String reff = ref.getKey() ;
            String ps = ref.getParent().toString();
            Log.e(TAG, "onDataChange for cont  "+ reff  +" size: "+dataSnapshot.getChildrenCount() +dataSnapshot.toString() );
            long FBkey=(Calendar.getInstance().getTimeInMillis());
            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                LocationRealm locationRealm = new LocationRealm();
                locationRealm.setLat(ds.getValue(LatLngMy.class).getLat());
                locationRealm.setLon(ds.getValue(LatLngMy.class).getLon());
                locationRealm.setAccuracy(ds.getValue(LatLngMy.class).getAccuracy());
                locationRealm.setSpeed(ds.getValue(LatLngMy.class).getSpeed());
                locationRealm.setFBkey(Long.parseLong(ds.getKey()));
//                if (ds.getValue(LatLngMy.class).getlastlocaltime() > 0)
//                    locationRealm.setLocaltimeupdate(ds.getValue(LatLngMy.class).getlastlocaltime());
                if (ds.getValue(LatLngMy.class).getTimestampLastLong() > 0)
                    locationRealm.setFBUpdated(ds.getValue(LatLngMy.class).getTimestampLastLong());

                locationRealm.setFBCreated(ds.getValue(LatLngMy.class).getTimestampCreatedLong());
                long ss= Calendar.getInstance().getTimeInMillis()-  locationRealm.getFBCreated();
                Log.e(TAG, "We find  for cont  "+ reff+" size: "+dataSnapshot.getChildrenCount()+" time:  "+ss );
                FBkey = locationRealm.getFBkey();
            }

            if (dataSnapshot.getChildrenCount() >0) ResetListener(dataSnapshot.getRef().getKey(),FBkey);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
