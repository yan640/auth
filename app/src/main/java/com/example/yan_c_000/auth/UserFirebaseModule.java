package com.example.yan_c_000.auth;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yan-c_000 on 10.11.2017.
 */
@Module(includes = ContextModule.class)
public class UserFirebaseModule {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Provides
    public User getUsers(Context context){
        final User uInfo = new User();
        SharedPref sharedPref = new SharedPref();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("user");

        //mFirebaseDatabase.child(sharedPref.GetPref(SharedPref.APP_PREFERENCES_FBID, context)).addValueEventListener(new ValueEventListener() {
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //showData(dataSnapshot);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("", "showData: name: " + ds.getValue(User.class).getName());
                    Log.d("", "showData: getPhone: " + ds.getValue(User.class).getPhone());

                    uInfo.setName(dataSnapshot.getValue(User.class).getName()); //set the name
                    uInfo.setPhone(dataSnapshot.getValue(User.class).getPhone()); //set the email
                    //uInfo.setTimeadd(dataSnapshot.getValue(User.class).getTimeadd()); //set the phone_num

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return uInfo  ;
    }

}
