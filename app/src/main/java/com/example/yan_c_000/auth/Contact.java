package com.example.yan_c_000.auth;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by yan-c_000 on 27.10.2017.
 */
@IgnoreExtraProperties
public class Contact implements Parcelable {
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

    public void setIdFireAuth(String idFireAuth) {
        this.idFireAuth = idFireAuth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact() {

    }
    private Contact(Parcel in) {
        name = in.readString();
        phone = in.readString();
        phonefull = in.readString();
        id = in.readInt();
        idFireAuth = in.readString();
        allowed = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(name);
        dest.writeString(phonefull);
        dest.writeInt(id);
        dest.writeString(idFireAuth);
        dest.writeByte((byte) (allowed ? 1 : 0));
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];

        }
    };

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