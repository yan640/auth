package com.example.yan_c_000.auth;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by yan-c_000 on 07.11.2017.
 */

public class SharedPref2 extends Application {

    public static final String APP_PREFERENCES  = "APP_PREFERENCES";
    public static final String APP_PREFERENCES_NAME  = "APP_PREFERENCES_NAME";
    public static final String APP_PREFERENCES_PHONE  = "APP_PREFERENCES_PHONE";
    public static final String APP_PREFERENCES_FBID  = "APP_PREFERENCES_FBID";
    public static final String APP_PREFERENCES_NEW_USER_BOOLEAN  = "APP_PREFERENCES_NEW_USER_BOOLEAN";
    static SharedPreferences mSettings;

    private static SharedPref2 instance;

    public static SharedPref2 getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }


    public   Boolean CheckPref (String PrefName){
        mSettings = PreferenceManager.getDefaultSharedPreferences(instance);
        if(mSettings.contains(PrefName)) {
            if(mSettings.getString(PrefName, "")!=""){
                return true;
            }



        }


        return false;

    }

    public    String GetPref (String PrefName ){
        //Context context = ;
        mSettings = PreferenceManager.getDefaultSharedPreferences(instance);
        if(mSettings.contains(PrefName)) {
            return mSettings.getString(PrefName, "") ;




        }


        return "";

    }



    public   Boolean GetPrefBool (String PrefName){

        mSettings = PreferenceManager.getDefaultSharedPreferences(instance);
        //mSettings =  context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);
        if(mSettings.contains(PrefName)) {
            return mSettings.getBoolean(PrefName, true) ;




        }


        return false;

    }

    void SetPref (String PrefName, String PrefValue){
        //mSettings =  context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);

        mSettings = PreferenceManager.getDefaultSharedPreferences(instance);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PrefName, PrefValue);
        editor.apply();
    }

    void SetPrefBool (String PrefName, Boolean PrefValue){

        mSettings = PreferenceManager.getDefaultSharedPreferences(instance);
        //mSettings =  context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PrefName, PrefValue);
        editor.apply();
    }


    void OnStartAppCheck (String PhoneNumber, String Uid) {
        if (GetPref(SharedPref2.APP_PREFERENCES_PHONE)=="" && GetPref(SharedPref2.APP_PREFERENCES_PHONE)!=PhoneNumber){
            SetPrefBool(SharedPref2.APP_PREFERENCES_NEW_USER_BOOLEAN, true );
            SetPref(SharedPref2.APP_PREFERENCES_PHONE, PhoneNumber );
            SetPref(SharedPref2.APP_PREFERENCES_FBID, Uid );
        }
    }

}
