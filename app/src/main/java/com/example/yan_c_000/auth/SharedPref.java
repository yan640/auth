package com.example.yan_c_000.auth;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by yan-c_000 on 07.11.2017.
 */

public class SharedPref extends Application {

    public static final String APP_PREFERENCES  = "APP_PREFERENCES";
    public static final String APP_PREFERENCES_NAME  = "APP_PREFERENCES_NAME";
    public static final String APP_PREFERENCES_PHONE  = "APP_PREFERENCES_PHONE";
    public static final String APP_PREFERENCES_FBID  = "APP_PREFERENCES_FBID";
    public static final String APP_PREFERENCES_NEW_USER_BOOLEAN  = "APP_PREFERENCES_NEW_USER_BOOLEAN";
    static SharedPreferences mSettings;
    public   Boolean CheckPref (String PrefName, Context context){
        mSettings =  context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);
        if(mSettings.contains(PrefName)) {
            if(mSettings.getString(PrefName, "")!=""){
                return true;
            }



        }


        return false;

    }

    public static    String GetPref (String PrefName, Context context){
        mSettings =  context.getSharedPreferences(APP_PREFERENCES, 0);
        if(mSettings.contains(PrefName)) {
            return mSettings.getString(PrefName, "") ;




        }


        return "";

    }
    public    String GetPrefFromService (String PrefName, Context context){
        mSettings =  context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);
        if(mSettings.contains(PrefName)) {
            return mSettings.getString(PrefName, "") ;




        }


        return "";

    }
//    public      String GetPrefFromService (String PrefName ){
//        //mSettings =    getSharedPreferences(APP_PREFERENCES,  MODE_PRIVATE);
//
//        mSettings =  getApplicationContext().getSharedPreferences(APP_PREFERENCES, Activity.MODE_PRIVATE);
//        //mSettings = PreferenceManager.getDefaultSharedPreferences(this);
//        if(mSettings.contains(PrefName)) {
//            return mSettings.getString(PrefName, "") ;
//
//
//
//
//        }
//
//
//        return "";
//
//    }

    public   Boolean GetPrefBool (String PrefName, Context context){
        mSettings =  context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);
        if(mSettings.contains(PrefName)) {
            return mSettings.getBoolean(PrefName, true) ;




        }


        return false;

    }

    void SetPref (String PrefName, String PrefValue, Context context){
        mSettings =  context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PrefName, PrefValue);
        editor.apply();
    }

    void SetPrefBool (String PrefName, Boolean PrefValue, Context context){
        mSettings =  context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PrefName, PrefValue);
        editor.apply();
    }


    void OnStartAppCheck (String PhoneNumber, String Uid, Context context) {
        if (GetPref(SharedPref.APP_PREFERENCES_PHONE, context)=="" && GetPref(SharedPref.APP_PREFERENCES_PHONE, context)!=PhoneNumber){
            SetPrefBool(SharedPref.APP_PREFERENCES_NEW_USER_BOOLEAN, true,  context );
            SetPref(SharedPref.APP_PREFERENCES_PHONE, PhoneNumber,  context );
            SetPref(SharedPref.APP_PREFERENCES_FBID, Uid,  context );
        }
    }

}
