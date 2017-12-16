package com.example.yan_c_000.auth;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by yan-c_000 on 27.10.2017.
 */
@IgnoreExtraProperties
public class Contact{
    public String name ;
    public String phone ;
    public String phonefull ;
    public int id;

    public String getPhonefull() {
        return phonefull;
    }

    public void setPhonefull(String phonefull) {
        this.phonefull = phonefull;
    }

    public String idFireAuth ;
    public boolean allowed;

    public Contact() {
    }

    public Contact(String name, String phone,String phonefull,  int id, String idFireAuth , boolean allowed) {
        this.name = name;
        this.phone = phone;
        this.phonefull = phonefull;
        this.id = id;
        this.idFireAuth = idFireAuth;
        this.allowed = allowed;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }

    public String getIdFireAuth() {
        return idFireAuth;
    }

    public boolean isAllowed() {
        return allowed;
    }
    //    public void setName(String name) {
//        _name = name;
//    }
//
//    public void setPhone(String phone) {
//        _phone = phone;
//    }
//
//    public void setMyId(int my_id) {
//        _my_id = my_id;
//    }
//    public void allowed(boolean allowed) {
//        allowed = allowed;
//    }
}