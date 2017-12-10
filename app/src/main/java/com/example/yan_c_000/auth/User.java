package com.example.yan_c_000.auth;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ravi Tamada on 07/10/16.
 * www.androidhive.info
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String phone;
    //public Date timeadd;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

//    public Date getTimeadd() {
//        return timeadd;
//    }
//    public void setTimeadd(Date timeadd) {
//        this.timeadd = timeadd;
//    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;



    }
}
