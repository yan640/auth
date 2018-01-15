package com.example.yan_c_000.auth.FireDatabase;

import android.content.Context;
import android.util.Log;

import com.example.yan_c_000.auth.Contact;
import com.example.yan_c_000.auth.Realm.Contacts;
import com.example.yan_c_000.auth.Realm.LocalRealmDB;
import com.example.yan_c_000.auth.Realm.LocationRealm;
import com.example.yan_c_000.auth.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.RealmResults;


public class UserListener {
    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseSend;
    private FirebaseDatabase mFirebaseInstance;
    Context context;
    public static final String TAG = UserListener.class.getName();
    SimpleDateFormat formating = new SimpleDateFormat("MM:dd:HH:mm:ss.SS");
    public interface Callback{
        void UserListenerCallBack(ArrayList<Contact> Users);

    }

    public Callback callback;




    public UserListener(Context mcontext ) {
        context=mcontext;
        if (context instanceof  Callback) {
            this.callback = ( Callback) context;
        }

        mFirebaseInstance = FirebaseDatabase.getInstance();

//        mLastLocationDatabase = mFirebaseInstance.getReference("LastLocation");


    }

    public void Listen(String userFBkey){
//        String now =  String.valueOf(Calendar.getInstance().getTimeInMillis() );
        mFirebaseDatabase = mFirebaseInstance.getReference("IncomePermissionRequest");
        mFirebaseDatabase.child(userFBkey).addValueEventListener(UserListener);


    }
    private final ValueEventListener UserListener = new  ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //showData(dataSnapshot);
            ArrayList<Contact> Users = new ArrayList<Contact>();
            Log.e(TAG, "onDataChange"  );
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Contact user = new Contact();
                user.setIdFireAuth(ds.getKey());
                user.setPhonefull(ds.getValue().toString());
                Users.add(user);

//                Log.e(TAG, "CreateContact phone"   + ds.getValue(User.class).getPhone() );

             //   LocalRealmDB.CreateContact(context, ds.getValue(User.class).getPhone(), ds.getValue(User.class).getName(), ds.getKey());

            }

            if (Users.size()>0)  callback.UserListenerCallBack(  Users );

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    public final ChildEventListener IncomePermissionRequestListener = new  ChildEventListener(){

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //showData(dataSnapshot);
            DatabaseReference ref  = dataSnapshot.getRef();
            String LocationFbkey = ref.getKey() ;
            String UserFBkey = ref.getParent().getKey().toString();
            //Log.e(TAG, "onChildAdded for cont  "+ reff  +" size: "+dataSnapshot.getChildrenCount()  );

            ArrayList<Contact> Users = new ArrayList<Contact>();
            Log.e(TAG, "ref"+ ref    );
            Log.e(TAG, "onChildAdded    UserFBkey  "+ UserFBkey+" LocationFbkey   "+ LocationFbkey+" size: "+dataSnapshot.toString() );
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                //Contact user = new Contact;
                  //user.s = ds.getKey();
//                user.setPhonefull(ds.getValue().toString());
//                Users.add(user);
            }

//            try {
//                if (!(dataSnapshot.getRef().getKey().isEmpty())
//                        && !(dataSnapshot.getRef().getKey() == null)
//                         && !(UserFBkey == null)                     ) {
//                    String[] user = new String[2];
//                    user[0] = dataSnapshot.getKey()
//                    LocationRealm locationRealm = new LocationRealm();
//                    locationRealm.setLat(dataSnapshot.getValue(LatLngMy.class).getLat());
//                    locationRealm.setLon(dataSnapshot.getValue(LatLngMy.class).getLon());
//                    locationRealm.setAccuracy(dataSnapshot.getValue(LatLngMy.class).getAccuracy());
//                    locationRealm.setSpeed(dataSnapshot.getValue(LatLngMy.class).getSpeed());
//                    locationRealm.setFBkey(Long.parseLong(dataSnapshot.getRef().getKey()));
////                    if (dataSnapshot.getValue(LatLngMy.class).getlastlocaltime() > 0)
////                        locationRealm.setLocaltimeupdate(dataSnapshot.getValue(LatLngMy.class).getlastlocaltime());
//                    if (dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong() > 0)
//                        locationRealm.setFBUpdated(dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong());
//
//                    locationRealm.setFBCreated(dataSnapshot.getValue(LatLngMy.class).getTimestampCreatedLong());
//                    long ss = Calendar.getInstance().getTimeInMillis() - locationRealm.getFBCreated();
//                    LocalRealmDB.UpdateLastLocationForContact(context, ref.getParent().getKey(), locationRealm);
//                    String phone =LocalRealmDB.UpdateLastLocationForContact(context, ref.getParent().getKey(), locationRealm);
//
//                    Log.e(TAG, "onChildAdded     We find  for cont  "+ UserFBkey+" size: "+dataSnapshot.getChildrenCount()+" time:  "+ss );
//
//
//
//                }


//            } catch (NullPointerException e) {
//                Log.e(TAG, "onChildAdded   NullPointerException");
//            }





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
//            try {
//                if (!(dataSnapshot.getRef().getKey().isEmpty())
//                        && !(dataSnapshot.getRef().getKey() == null)
//                        && Long.parseLong(dataSnapshot.getRef().getKey()) > 1012567391
//                        && !(UserFBkey == null)
//
//                        ) {
//
//                    LocationRealm locationRealm = new LocationRealm();
//                    locationRealm.setLat(dataSnapshot.getValue(LatLngMy.class).getLat());
//                    locationRealm.setLon(dataSnapshot.getValue(LatLngMy.class).getLon());
//                    locationRealm.setAccuracy(dataSnapshot.getValue(LatLngMy.class).getAccuracy());
//                    locationRealm.setSpeed(dataSnapshot.getValue(LatLngMy.class).getSpeed());
//                    locationRealm.setFBkey(Long.parseLong(dataSnapshot.getRef().getKey()));
////                    if (dataSnapshot.getValue(LatLngMy.class).getlastlocaltime() > 0)
////                        locationRealm.setLocaltimeupdate(dataSnapshot.getValue(LatLngMy.class).getlastlocaltime());
//                    if (dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong() > 0)
//                        locationRealm.setFBUpdated(dataSnapshot.getValue(LatLngMy.class).getTimestampLastLong());
//
//                    locationRealm.setFBCreated(dataSnapshot.getValue(LatLngMy.class).getTimestampCreatedLong());
//                    long ss = Calendar.getInstance().getTimeInMillis() - locationRealm.getFBCreated();
//                    String phone = LocalRealmDB.UpdateLastLocationForContact(context, ref.getParent().getKey(), locationRealm);
//
//                    Log.e(TAG, "onChildChanged   We find  for cont  " + UserFBkey + " size: " + dataSnapshot.getChildrenCount() + " time:  " + ss);
//
//                }
//
//
//            } catch (NullPointerException e) {
//                Log.e(TAG, "onChildChanged   NullPointerException");
//            }



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


    public void SendPermissionRequest(String myid,String MyPhone, String phone){

        mFirebaseDatabaseSend = mFirebaseInstance.getReference("PermissionRequest");
        mFirebaseDatabaseSend.child(myid).child(phone).setValue(MyPhone);
    }

}
