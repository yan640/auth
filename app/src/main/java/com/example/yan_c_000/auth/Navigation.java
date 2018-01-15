package com.example.yan_c_000.auth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yan_c_000.auth.Contacts.FixPhoneNumber;
import com.example.yan_c_000.auth.FireDatabase.FBLocationListener;
import com.example.yan_c_000.auth.FireDatabase.UserListener;
import com.example.yan_c_000.auth.Realm.Contacts;
import com.example.yan_c_000.auth.Realm.LocalRealmDB;
import com.example.yan_c_000.auth.Realm.LocationRealm;
import com.example.yan_c_000.auth.RealmChecker.LocationRealmChecker;
import com.example.yan_c_000.auth.RealmChecker.RealmChecker;
import com.example.yan_c_000.auth.Service_location.GPS_Service_once_fusion_last;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmResults;

public class Navigation extends AppCompatActivity
        implements RemoteToLocalLoader.Callback, FBLocationListener.Callback, UserListener.Callback, NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private TextView textView2;
    private Button buttRefresh;
    private GoogleMap mMap;
    public static final int NUM_GRAPH_POINTS = 2;
    public static int NUM_NOW = 0;



    private List<UltraHeight> lastData;
    private UltraHeightSingleton mData;
    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference FireRefLat;
    private FirebaseDatabase mFirebaseInstance;
    private final String TAG = "Navigation.Main";
    SimpleDateFormat formating = new SimpleDateFormat("HH:mm:ss.SS");
    String path = "";
    String mJsonOutput;
    protected static SharedPreferences prefs;
    public static final String NAME = "name";
    public Menu menu;
    public Menu topChannelMenu;

    private BroadcastReceiver broadcastReceiver;
    private String userId;
    private FirebaseAuth mAuth;
    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 300;
    private static final String AddNewContact = "AddNewContact";
    private static final String IncomingPermissions = "IncomingPermissions";
    ArrayList<Contact> cont_nav_drawer = new ArrayList<>();
    ArrayList<Contact> cont = new ArrayList<>();
    ArrayList<Contact> Permissions = new ArrayList<>();
    public String CurrentCont;
    public RemoteToLocalLoader remoteToLocalLoader;
    public Marker marker;
    int lastNavigationItemId=0;
    int requestCode = 123;
    Map<MenuItem, String> MenuPhoneSaver = new HashMap<MenuItem, String>();

    public void setMenuItemTag(MenuItem item, String phone)
    {
        MenuPhoneSaver.put(item, phone);
    }

    // returns null if tag has not been set(or was set to null)
    public String getMenuItemTag(MenuItem item )
    {
        return MenuPhoneSaver.get(item);
    }

    @Override
    public void RemoteToLocalLoaderCallingBack( ) {
        RealmResults<Contacts> results = LocalRealmDB.GetAllContacts(this);
        menu.clear();
        topChannelMenu = menu.addSubMenu("Contacts");
        setMenuItemTag(topChannelMenu.add("Add new..."),AddNewContact);
        for (Contact c: cont) {
            for (Contacts contacts : results) {
                if (c.getPhonefull().equals(contacts.getPhone())) {
                    Log.e(TAG, "Yes phone equal!!!!!!  " + c.getPhone() + "equal  " + contacts.getPhone());
                    LocalRealmDB.ChangeContactName(this, contacts,c.getName());



                }

            }

        }

        for (Contacts contacts : results) {
            SharedPref2 sharedPref2 = new SharedPref2();

            if (sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_PHONE).equals(contacts.getPhone())) {
                LocalRealmDB.ChangeContactName(this,contacts, "My Location");
            }

            setMenuItemTag(topChannelMenu.add(contacts.getName()),contacts.getPhone());





        }

        FBLocationListener fbLocationListener = new FBLocationListener(this);
        SharedPref2 sharedPref2 = new SharedPref2();
        String Myid = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID  );

        UserListener userListener= new UserListener(this);
        userListener.Listen(Myid);

    }

    @Override
    public void UserListenerCallBack( ArrayList<Contact> Users) {

        if (Users.size()>0) {
            for (Contact c: cont) {
                for (Contact contacts : Users) {
                    if (c.getPhonefull().equals(contacts.getPhonefull())) {
                        Log.e(TAG, "Yes phone equal!!!!!!  " + c.getPhone() + "equal  " + contacts.getPhone());

                        contacts.setName(c.getName());


                    }

                }

            }
            Permissions.addAll(Users);
            setMenuItemTag(topChannelMenu.add("Incoming requsts +"+Users.size() ),IncomingPermissions);
        }
//        Permissions = menu.addSubMenu("Permissions");
//         //setMenuItemTag(topChannelMenu.add("Add new..."),AddNewContact);
//        for (Contact c: cont) {
//            for (String[] contacts : Users) {
//                if (c.getPhonefull().equals(contacts[1])) {
//                    Log.e(TAG, "Yes phone equal!!!!!!  " + c.getPhone() + "equal  " + contacts[1]);
//                    contacts[2] = c.getName();
//
//
//
//                }
//
//            }
//
//        }
//
//        for (String[] contacts : Users) {
//
//
//            setMenuItemTag(Permissions.add(contacts[2]),contacts[1]);
//
//
//
//
//
//        }
//
//        FBLocationListener fbLocationListener = new FBLocationListener(this);

    }



    @Override
    public void FBLocationListenerCallBack(String phone ) {
        if ((!CurrentCont.isEmpty()) && CurrentCont.equals(phone)){
          LocationRealm loc =  LocalRealmDB.FindContact(this, phone).location.last();
            String time= getTimeFromRealm(loc);
            LatLng endLatLng = new LatLng(loc.getLat(), loc.getLon());




                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 12));
                marker.setTitle(time);


                textView2.setText(time);


        }

        //topChannelMenu = menu.;


    }



    @Override
    public void FBLocationListenerCallBackNewLocation(String phone ) {
        if ((!CurrentCont.isEmpty()) && CurrentCont.equals(phone)) {
            LocationRealm loc = LocalRealmDB.FindContact(this, phone).location.last();
            String time = getTimeFromRealm(loc);
            LatLng endLatLng = new LatLng(loc.getLat(), loc.getLon());
            List<LatLng> poly = new ArrayList<LatLng>();


            marker.setAlpha((float) 0.2);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 12));
            LatLng firstLatLng = marker.getPosition();
            poly.add(firstLatLng);
            poly.add(endLatLng);


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 12));
            marker = mMap.addMarker(new MarkerOptions()
                    .position(endLatLng)
                    .alpha((float) 1)
                    .title(time));


            mMap.addPolyline(new PolylineOptions()
                    .addAll(poly)
                    .color(Color.BLACK));

            textView2.setText(time);


        }

        //topChannelMenu = menu.;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mData = UltraHeightSingleton.get(this);
        setSupportActionBar(toolbar);
//        if  (prefs.getString(Navigation.NAME, "none") == "none") {
//
//        }
//        else {
//            prefs.edit().putString(Navigation.NAME, path);
//        }
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
       // mFirebaseInstance.setPersistenceEnabled(true);
        mFirebaseInstance = FirebaseDatabase.getInstance();

//         FireRefLat = mFirebaseInstance.getReference("latlng/ftFnDRa6HOe8xNiEQ1xb3fH7ALt2");
//        FireRefLat.removeValue( );
//        mFirebaseDatabase = mFirebaseInstance.getReference("user");
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        String phone = currentUser.getPhoneNumber();
//        userId = currentUser.getUid();
//        User user = new User(phone, phone, Calendar.getInstance().getTime());
//        mFirebaseDatabase.child(userId).setValue(user);



//          LocalRealmDB.removeAllLocations(this);
//        Realm realm = null;
//        realm.init(this);
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
//        Realm.setDefaultConfiguration(realmConfiguration);
//        realm = Realm.getDefaultInstance();
//        final RealmResults<Contacts> results = realm.where(Contacts.class).findAll();
//
//        RealmResults<LocationRealmChecker> realmCheckers = RealmChecker.FindLastLocationsChecker(this,Calendar.getInstance().getTimeInMillis());
//        ArrayList<LocationRealm> locations = new ArrayList();
//        for (LocationRealmChecker checker : realmCheckers){
//            LocationRealm locationRealm   = new LocationRealm();
//            locationRealm.setLat(checker.getLat());
//            locationRealm.setLon(checker.getLon());
//            locationRealm.setAccuracy(checker.getAccuracy());
//            locationRealm.setSpeed(checker.getSpeed());
//            locationRealm.setFBkey(checker.getFBkey() );
//
//
//
//            if (checker.getTimeLast()>0 ) locationRealm.setLocaltimeupdate(checker.getTimeLast());
//            //if (ds.getValue(LatLngMy.class).getTimestampLastLong()>0 ) locationRealm.setFBUpdated(ds.getValue(LatLngMy.class).getTimestampLastLong());
//
//            //locationRealm.setFBCreated(ds.getValue(LatLngMy.class).getTimestampCreatedLong());
//
//            //ds.getValue(ServerValue.TIMESTAMP);
//            locations.add(locationRealm);
//        }
//        LocalRealmDB.SaveLocations(this,results.get(0),locations);


        textView2 = findViewById(R.id.textView2);
        buttRefresh= findViewById(R.id.refresh);
        setAlarm();

        // get reference to 'users' node


        // store app title to 'app_title' node
        //mFirebaseInstance.getReference("app_title").setValue("Database");

        // send username to firebase
        // todo check before send. maybe firebase already have this data


        // getting contacts dagger
//        UserDBApplication component = DaggerUserDBApplication.builder()
//                .contextModule(new ContextModule(this))
//                .build();
        //user = component.getUser();
        //LocalRealmDB.CreateContact(this,phone, phone);
        //LocalRealmDB.removeAllContact(this);

        //LocalRealmDB.FindContact(this,phone);




        if (!runtime_permissions())
            enable_buttons();
        initMap();

        SharedPref2 sharedPref2 = new SharedPref2();
        remoteToLocalLoader = new RemoteToLocalLoader(this,  sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID)  );
        if (sharedPref2.GetPrefBool(SharedPref2.APP_PREFERENCES_NEW_USER_BOOLEAN)){
            //TODO new user or new phone number, delet all info and initialisize from  0
            remoteToLocalLoader.CheckMyUser();
            sharedPref2.SetPrefBool(SharedPref2.APP_PREFERENCES_NEW_USER_BOOLEAN, false );
        }

        if (!contacts_permissions()) ;
        sendContats();


        // RemoteToLocalLoader.Load(this);


//        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                showData(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        //getAll(this);


//        textView2.setText("" +  sharedPref.GetPref(SharedPref.APP_PREFERENCES_PHONE, this));
//        textView2.setText("" +  sharedPref.GetPref(SharedPref.APP_PREFERENCES_FBID, this));




    }



    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    private void sendContats() {

        cont = getAll(this);



    }

    public ArrayList<Contact> getAll(Context context) {
        ContentResolver cr = context.getContentResolver();

        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{PHONE_NUMBER, PHONE_CONTACT_ID},
                null,
                null,
                null
        );
        if (pCur != null) {
            if (pCur.getCount() > 0) {
                HashMap<Integer, ArrayList<String>> phones = new HashMap<>();
                while (pCur.moveToNext()) {
                    Integer contactId = pCur.getInt(pCur.getColumnIndex(PHONE_CONTACT_ID));

                    ArrayList<String> curPhones = new ArrayList<>();

                    if (phones.containsKey(contactId)) {
                        curPhones = phones.get(contactId);

                    }
                    curPhones.add(pCur.getString(0));

                    phones.put(contactId, curPhones);

                }
                Cursor cur = cr.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        new String[]{CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER},
                        HAS_PHONE_NUMBER + " > 0",
                        null,
                        DISPLAY_NAME + " ASC");

                mFirebaseDatabase = mFirebaseInstance.getReference("Contacts");


                if (cur != null) {
                    if (cur.getCount() > 0) {
                        ArrayList<Contact> contacts = new ArrayList<>();
                        int jk = 0;
                        while (cur.moveToNext()) {
                            int id = cur.getInt(cur.getColumnIndex(CONTACT_ID));
                            if (phones.containsKey(id)) {

                                for (int i = 0; i < phones.get(id).size(); i++) {
//                                    Contact con = new Contact();
//                                    con.setMyId(id);
//                                    con.setName(cur.getString(cur.getColumnIndex(DISPLAY_NAME)));
                                    //con.setPhone(TextUtils.join(",", phones.get(id).toArray()));
                                    //phones.get(id).get(i).replaceAll("[ -()]","");
                                    String phone = PhoneNumberUtils.formatNumber(phones.get(id).get(i).replaceAll("[ -()]", ""));
                                    if (PhoneNumberUtils.isGlobalPhoneNumber(phone) & phone.length() > 10) {
                                        String phonefull=phone;
                                        phone = phone.substring(phone.length() - 10, phone.length());

                                        Contact con = new Contact(cur.getString(cur.getColumnIndex(DISPLAY_NAME)), phone, phonefull, id, "", false);
                                        contacts.add(con);
                                        //mFirebaseDatabase.child(userId).child(phone).setValue(con);
                                        SharedPref2 sharedPref2 = new SharedPref2();
                                        String userFbId = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID);
                                        mFirebaseDatabase.child(userFbId).child(phone).setValue(con);
                                        //Log.e("contact", jk+" : "+phone);
                                        jk++;
                                    }
//                                    Log.e("contact", jk+" : "+phones.get(id).get(i).replaceAll("[ -()]",""));
//                                    jk++;

                                }


                            }
                        }
                        return contacts;
                    }
                    cur.close();
                }
            }
            pCur.close();
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap map) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        //map.setMyLocationEnabled(true);

//                .addMarker(new MarkerOptions()
//                .position(new LatLngMy(0, 0))
//                .title("Marker"));
        mMap = map;
        RealmResults<LocationRealmChecker> results = RealmChecker.FindLastLocationsChecker(this, Calendar.getInstance().getTimeInMillis()  );
        lastData = mData.getLastData(NUM_GRAPH_POINTS);
        int sd = results.size();
        List<LatLng> poly = new ArrayList<LatLng>();

        if (results.size()>0) {
            //for (LocationRealmChecker lrm : results) {
            for (int i = results.size() - 1; i >0 && i > sd - 15; i--) {
                //textView2.append("\n" + lastData.get(i).getDate()  + "  :" + lastData.get(i).getHeight() + " --" + lastData.get(i).getLat());


                LatLng endLatLng = new LatLng(results.get(i).getLat(), results.get(i).getLon());
                poly.add(endLatLng);
                String time = formating.format(results.get(i).getFBkey());
                if (results.get(i).getTimeLast()>0) time = time+"-"+ String.valueOf(formating.format(results.get(i).getTimeLast()));
                MarkerOptions hh;
               if (results.get(i).getFBTimeStamp()>0 ) {
                     hh = new MarkerOptions()
                           .position(endLatLng)
                           .title(time)
                             .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                           .snippet("Accur:" + String.valueOf(((long) results.get(i).getAccuracy())) + " Speed:" + String.valueOf(((long) results.get(i).getSpeed())));
               }
               else {hh = new MarkerOptions()
                       .position(endLatLng)
                       .title(time)
                       .snippet("Accur:" + String.valueOf(((long) results.get(i).getAccuracy())) + " Speed:" + String.valueOf(((long) results.get(i).getSpeed())));


               }
               mMap.addMarker(hh);


                if (i == results.size() - 1) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 12));
                }
                mMap.addPolyline(new PolylineOptions()
                        .addAll(poly)
                        .color(Color.BLACK));
                //  .title(formating.format(lastData.get(i).getDate())));


            }
        }
        //mMap = map;
    }

    private void setAlarm() {
        Context ctx = getApplicationContext();
/** this gives us the time for the first trigger.  */
        Calendar cal = Calendar.getInstance();
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60 * 2; // 5/10 minutes in milliseconds
        //Intent serviceIntent = new Intent(ctx, GPS_Service_once.class);
        Intent serviceIntent = new Intent(ctx, GPS_Service_once_fusion_last.class);
// make sure you **don't** use *PendingIntent.getBroadcast*, it wouldn't work
        //serviceIntent.setAction(GPS_Service_once.ACTION_ALARM_RECEIVER);
        serviceIntent.setAction(GPS_Service_once_fusion_last.ACTION_ALARM_RECEIVER);
        PendingIntent servicePendingIntent =
                PendingIntent.getService(ctx,
                        0, // integer constant used to identify the service
                        serviceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running
// there are other options like setInexactRepeating, check the docs
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,//type of alarm. This one will wake up the device when it goes off, but there are others, check the docs
                cal.getTimeInMillis(),
                interval,
                servicePendingIntent);
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    private boolean contacts_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return true;
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    android.Manifest.permission.READ_CONTACTS)) {
//
//                // TODO Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{android.Manifest.permission.READ_CONTACTS},
//                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
        }
        return false;
    }


//    if( getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, resultValue);

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    contacts_permissions();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    enable_buttons();
                } else {
                    runtime_permissions();
                }
            }

        }
    }

    private void enable_buttons() {
        buttRefresh.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                         FireRefLat = mFirebaseInstance.getReference("latlng/STUrTs2KdtguGRHIoYpvWBwP1K83");
         FireRefLat.addListenerForSingleValueEvent(UserListener);
                SharedPref2 sharedPref2 = new SharedPref2();
                remoteToLocalLoader = new RemoteToLocalLoader(Navigation.this,  sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID)  );
                remoteToLocalLoader.Load();







            }});
    }

    private final ValueEventListener UserListener = new  ValueEventListener(){

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //showData(dataSnapshot);
            FireRefLat = mFirebaseInstance.getReference("latlng/STUrTs2KdtguGRHIoYpvWBwP1K83");
            long cutoff = Calendar.getInstance().getTimeInMillis() - 72 * 60 * 60 * 1000;
            Log.e(TAG, "onDataChange"  );
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Log.e(TAG, "CreateContact phone"   + ds.getValue(User.class).getPhone() );
                if (Long.parseLong(ds.getKey())<cutoff) FireRefLat.child(ds.getKey()).removeValue();

            }


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int request_Code, int resultCode, Intent data) {
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {
                String phone = data.getStringExtra("phone");
               // Log.e(TAG, "Yes  !!!!!!  " + phone);
                //TODO send permissionRequest
//                if (PhoneNumberUtils.isGlobalPhoneNumber(phone) & phone.length() > 10) {
//
//                    phone = phone.substring(phone.length() - 10, phone.length());
//
//                }


                phone =  FixPhoneNumber.FixPhoneNumber(this,phone);
                SharedPref2 sharedPref2 = new SharedPref2();
                String MyPhone = FixPhoneNumber.FixPhoneNumber(this,sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_PHONE  ));
                String Myid = sharedPref2.GetPref(SharedPref2.APP_PREFERENCES_FBID  );
//                if (PhoneNumberUtils.isGlobalPhoneNumber(MyPhone) & MyPhone.length() > 10) {
//
//                    MyPhone = MyPhone.substring(MyPhone.length() - 10, MyPhone.length());
//
//                }

                UserListener userListener= new UserListener(this);
                userListener.SendPermissionRequest(Myid,MyPhone,phone);

            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        MenuItem last =menu.getItem(lastNavigationItemId);
        last.setChecked(false);
        lastNavigationItemId = item.getItemId();

        CurrentCont = getMenuItemTag(item);
        if (CurrentCont.equals( AddNewContact)) {
            //TODO открыть меню со всеми контактами . отправить в ФБ. Написать функцию создания закрытого раздела в базе. Настроить мониторинг своего раздела базы на получение запроса. Получать локации только из списка разрешенных контактов.
            Intent intent = new Intent(this, GetContact.class);

            intent.putExtra("com.example.yan_c_000.auth.Contact", cont);

            startActivityForResult(intent,requestCode);
        }
        else if (CurrentCont.equals( IncomingPermissions)) {
             Intent intent = new Intent(this, GetContact.class);

            intent.putExtra("com.example.yan_c_000.auth.Contact", Permissions);

            startActivityForResult(intent,requestCode);
        }

        else {
            item.setChecked(true);


            RealmList<LocationRealm> locationRealms = LocalRealmDB.GetLocations(this, CurrentCont);

            textView2.setText("");
            List<LatLng> poly = new ArrayList<LatLng>();
            setTitle(item.getTitle());
            mMap.clear();
            // List<MarkerOptions> markers = new
            int sd = locationRealms.size();


            if (locationRealms.size() > 0) {
                //for (LocationRealmChecker lrm : results) {
                for (int i = (locationRealms.size() - 1); i > 0 && i > sd - 15; i--) {

                    LocationRealm loc = locationRealms.get(i);
                    String time = getTimeFromRealm(loc);
                    LatLng endLatLng = new LatLng(loc.getLat(), loc.getLon());
                    poly.add(endLatLng);


                    if ((locationRealms.size() - 1) == i) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 12));
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(endLatLng)
                                .alpha((float) 1)
                                .title(time));


                        // textView2.append("\n"+"if"+loc.getLat() +" "+ loc.getLon() +" "+ loc.getFBkey());
                    } else {


                        marker = mMap.addMarker(new MarkerOptions()
                                .position(endLatLng)
                                .alpha((float) 0.2)
                                .title(time));

                        // textView2.append("\n"+"else"+loc.getLat() +" "+ loc.getLon() +" "+loc.getFBkey());
                    }
                }

            }

            mMap.addPolyline(new PolylineOptions()
                    .addAll(poly)
                    .color(Color.BLACK));



        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getTimeFromRealm (LocationRealm loc ){
        String time="";
        if (loc.getFBUpdated()>0)               time = formating.format(loc.getFBCreated())+"-"+ formating.format(loc.getFBUpdated());
        else         time = formating.format(loc.getFBCreated()) ;

        //        else if ( loc.getFBCreated()>0)         time = formating.format(loc.getFBCreated()) ;
//        else if ( loc.getLocaltimeupdate()>0 )  time = formating.format(loc.getFBkey())+"-"+ formating.format(loc.getLocaltimeupdate());
//        else                                    time = formating.format(loc.getFBkey()) ;

        return time;
    }




    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Calendar dating = Calendar.getInstance();
                    //textView2.append("\n" + formating.format(dating.getTime()) + ": " + intent.getExtras().get("coordinates"));
                    String data =  intent.getExtras().get("coordinates").toString();
                    String latdata = intent.getExtras().get("getLatitude").toString();
                    String  Accuracy =  intent.getExtras().get("getAccuracy").toString() ;
                    String  speed = intent.getExtras().get("speed").toString();
                    String  FBkey = intent.getExtras().get("FBkey").toString();
                    String  distance = intent.getExtras().get("distance").toString();
                     textView2.append("\n" +FBkey+ Accuracy +"  " + speed+distance);
                    double height, Lat;
                    try {
                        height = Double.valueOf(data);
                        Lat = Double.valueOf(latdata);
                        double lon = (double) intent.getExtras().get("coordinates");
                        double lat = (double) intent.getExtras().get("getLatitude");
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lon))
                                .title(formating.format(Calendar.getInstance().getTime())));
                        lastData.add(new UltraHeight(height, Lat));
                        mData.addItem(height, Lat);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Wrong float to int format " + e);
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

}
