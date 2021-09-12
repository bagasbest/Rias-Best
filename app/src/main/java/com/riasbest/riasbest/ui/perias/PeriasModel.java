package com.riasbest.riasbest.ui.perias;

import android.os.Parcel;
import android.os.Parcelable;

public class PeriasModel implements Parcelable {

    private String name;
    private String uid;
    private String username;
    private String email;
    private String role;
    private String address;
    private String dp;
    private String rekening;

    public PeriasModel() {}

    protected PeriasModel(Parcel in) {
        name = in.readString();
        uid = in.readString();
        username = in.readString();
        email = in.readString();
        role = in.readString();
        address = in.readString();
        dp = in.readString();
        rekening = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(role);
        dest.writeString(address);
        dest.writeString(dp);
        dest.writeString(rekening);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PeriasModel> CREATOR = new Creator<PeriasModel>() {
        @Override
        public PeriasModel createFromParcel(Parcel in) {
            return new PeriasModel(in);
        }

        @Override
        public PeriasModel[] newArray(int size) {
            return new PeriasModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getRekening() {
        return rekening;
    }

    public void setRekening(String rekening) {
        this.rekening = rekening;
    }
}
