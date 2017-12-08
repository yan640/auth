package com.example.yan_c_000.auth.Realm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.yan_c_000.auth.FireDatabase.FirebaseSender;
import com.example.yan_c_000.auth.SharedPref2;
import com.google.common.base.Strings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by yan-c_000 on 15.11.2017.
 */

public class LocalRealmDB extends Activity {

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

    private void showStatus(String txt) {
        Log.i(TAG, txt);


    }






     public static void CreateContact(Context context, final String phone,final String name,final String key) {
        if (!Strings.isNullOrEmpty(phone) && !Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(key)  ) {
            Realm realm = realmInit(context);
            final RealmResults<Contacts> results = realm.where(Contacts.class).equalTo("phone", phone).findAll();
            switch (results.size()) {
                case 0:
                    Log.e(TAG, "doesn't find contact with phone: " + phone + "  And we create one ");
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // Add a person
                            Contacts contact = realm.createObject(Contacts.class);
                            contact.setPhone(phone);
                            contact.setName(name);
                            contact.setKey(key);


                        }
                    });
                    break;
                case 1:
                    Log.e(TAG, "We find contact with phone: " + phone);
                    String name0 = results.get(0).getName();

                    if (!name.equals(results.get(0).getName())) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                results.get(0).setName(name);


                            }
                        });
                        Log.e(TAG, "We find contact with phone: " + results.get(0).getPhone() + "and change name from  " + results.get(0).getName() + "  to  " + name);
                    }
                    if (!key.equals(results.get(0).getKey())) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                results.get(0).setKey(key);


                            }
                        });
                        Log.e(TAG, "We find contact with phone: " + results.get(0).getPhone() + "and change key from  " + results.get(0).getKey() + "  to  " + key);
                    }


                    break;
                default:
                    Log.e(TAG, "We find contact with phone: " + phone + "but we have results.size() = " + results.size() + "  so we delete all contacts!!!! ");
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(Contacts.class);
                        }
                    });
                    break;
            }
        }
    }
    public static Contacts   FindContact(Context context, String phone) {
        Realm realm = realmInit(context);
        RealmResults<Contacts> results = realm.where(Contacts.class).equalTo("phone", phone).findAll();
        switch (results.size()){
            case 0:
                Log.e(TAG, "doesn't find contact with phone: " + phone);
                break;
            case 1:

                Log.e(TAG, "We find contact with phone: " + results.get(0).getPhone()+ "with phone: " +results.get(0).getName() + results.get(0).getKey());
            return results.get(0) ;

            default:
                Log.e(TAG, "We find contact with phone: " + phone   + "but we have results.size() = " + results.size());
                break;
        }
        return results.get(0) ;

    }

    public static void removeAllContact(Context context) {
        Realm realm = realmInit(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Contacts.class);
            }
        });
    }
    public static RealmResults<Contacts>  GetAllContacts (Context context){
        Realm realm = realmInit(context);
        RealmResults<Contacts> results = realm.where(Contacts.class).findAll();
        return results;
    }

    public static void removeLocationsForContact(Context context,final Contacts contact ) {
        Realm realm = realmInit(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                contact.location.deleteAllFromRealm();

            }
        });
    }
    public static void removeAllLocations(Context context) {
        Realm realm = realmInit(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(LocationRealm.class);
            }
        });
    }

    public static void  SaveLocations(Context context,final Contacts contact,final ArrayList<LocationRealm> locations) {
        removeLocationsForContact(context,  contact);
        Realm realm = realmInit(context);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (contact.getLocation().size() == 0) {
                    for (LocationRealm loc : locations) {

                        contact.location.add(loc);

                    }
                } else {
                    LocationRealm LastLocation = contact.getLocation().last();

                    for (LocationRealm loc : locations) {
                        if (LastLocation.getFBkey() < loc.getFBkey()) {
                            contact.location.add(loc);
                        }
                    }

                }


            }
        });
//        realm.beginTransaction();
//        realm.insert(locations);
//        realm.commitTransaction();

    }


    public static void UpdateMyLocations(Context context, final Contacts contact, final ArrayList<LocationRealm> locations) {
        Realm realm = realmInit(context);

        final RealmList<LocationRealm> locationRealms = GetLocationsAndDelete3dayOld(context, contact);
        final boolean[] Updated = new boolean[locations.size()];
        for (int i=0; i<locations.size();i++ ) {
            final LocationRealm locFB =locations.get(i);
            for (final LocationRealm locRealm : locationRealms) {
                if (locFB.getFBkey() == locRealm.getFBkey()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            locRealm.setFBCreated(locFB.getFBCreated());
                            if (locFB.getFBUpdated() > 0)
                                locRealm.setFBUpdated(locFB.getFBUpdated());

                        }
                    });
                    Updated[i]=true;
                }


            }
            if (!Updated[i]==true) Updated[i]=false;


        }



        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (locations.size() > 0) {
                    for (int i=0; i<locations.size();i++ ) {
                        if (Updated[i]==false){
                              LocationRealm locFB2 = locations.get(i);
                            contact.location.add(locFB2);
                        }

                    }
                }
            }
        });
        FirebaseSender.SendLocationsWithoutTimestamp(contact.getLocation());


//        realm.beginTransaction();
//        realm.insert(locations);
//        realm.commitTransaction();

    }

    public static void GetAllLocations(Context context, final Contacts contact ){
        Log.e(TAG, "contact.getPhone: " + contact.getPhone());

        for (LocationRealm loc : contact.getLocation()) {
            Log.e(TAG, "contact.getPhone: " + loc.getLat()+   " " + loc.getLon()+   "   size "+ contact.getLocation().size());
        }


    }

    public void AddLocation() {}
    public static RealmList<LocationRealm> GetLocations(Context context, final String phone) {

        Contacts contact = FindContact(context, phone);

        for (LocationRealm loc : contact.getLocation()) {
            Log.e(TAG, "contact.getPhone: " + loc.getLat()+   " " + loc.getLon()+   "   size "+ contact.getLocation().size());
        }
        return contact.getLocation();
    }

    public static RealmList<LocationRealm> GetLocationsAndDelete3dayOld(Context context,final  Contacts contact) {
        Realm realm = realmInit(context);
        final RealmList<LocationRealm> locationRealms = contact.getLocation();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (contact.getLocation().size() > 0) {
                    for (LocationRealm loc : locationRealms) {
                        if (!(loc.getFBkey() > (Calendar.getInstance().getTimeInMillis() - 172800000)))
                            loc.deleteFromRealm();
                    }
                }
            }
        });



        return contact.getLocation();
    }



}
