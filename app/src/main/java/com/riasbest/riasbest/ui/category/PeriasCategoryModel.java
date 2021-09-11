package com.riasbest.riasbest.ui.category;

import android.os.Parcel;
import android.os.Parcelable;

public class PeriasCategoryModel implements Parcelable {

    private String name;
    private String price;
    private String category;
    private String dp;
    private String uid;
    private String periasId;


    public PeriasCategoryModel(){}

    protected PeriasCategoryModel(Parcel in) {
        name = in.readString();
        price = in.readString();
        category = in.readString();
        dp = in.readString();
        uid = in.readString();
        periasId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(category);
        dest.writeString(dp);
        dest.writeString(uid);
        dest.writeString(periasId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PeriasCategoryModel> CREATOR = new Creator<PeriasCategoryModel>() {
        @Override
        public PeriasCategoryModel createFromParcel(Parcel in) {
            return new PeriasCategoryModel(in);
        }

        @Override
        public PeriasCategoryModel[] newArray(int size) {
            return new PeriasCategoryModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPeriasId() {
        return periasId;
    }

    public void setPeriasId(String periasId) {
        this.periasId = periasId;
    }
}
