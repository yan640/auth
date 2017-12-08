package com.example.yan_c_000.auth.Realm;

import android.location.Location;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by yan-c_000 on 15.11.2017.
 */

public class Contacts extends RealmObject {
    private String name;
    //@Required
    private String phone;
    //@Required
    private String key;

    // Other objects in a one-to-one relation must also subclass RealmObject

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    // One-to-many relations is simply a RealmList of the objects which also subclass RealmObject
    public RealmList<LocationRealm> location;

    public String getName() {
        return name;
    }

    public RealmList<LocationRealm> getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public RealmList<Location> getLocations() {
//        return Location;
//    }
//
//    public void setLocations(RealmList<Location> locations) {
//        Location = locations;
//    }
}
