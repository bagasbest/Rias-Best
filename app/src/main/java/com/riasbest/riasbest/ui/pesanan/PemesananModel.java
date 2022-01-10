package com.riasbest.riasbest.ui.pesanan;

import android.os.Parcel;
import android.os.Parcelable;

public class PemesananModel implements Parcelable {

    private String customerId;
    private String customerName;
    private String customerEmail;
    private String category;
    private String dp;
    private String dateTime;
    private String pelaksanaan;
    private String periasName;
    private String periasId;
    private String price;
    private String paymentProof;
    private String orderId;
    private String status;

    public PemesananModel(){}


    protected PemesananModel(Parcel in) {
        customerId = in.readString();
        customerName = in.readString();
        customerEmail = in.readString();
        category = in.readString();
        dp = in.readString();
        dateTime = in.readString();
        pelaksanaan = in.readString();
        periasName = in.readString();
        periasId = in.readString();
        price = in.readString();
        paymentProof = in.readString();
        orderId = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerId);
        dest.writeString(customerName);
        dest.writeString(customerEmail);
        dest.writeString(category);
        dest.writeString(dp);
        dest.writeString(dateTime);
        dest.writeString(pelaksanaan);
        dest.writeString(periasName);
        dest.writeString(periasId);
        dest.writeString(price);
        dest.writeString(paymentProof);
        dest.writeString(orderId);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PemesananModel> CREATOR = new Creator<PemesananModel>() {
        @Override
        public PemesananModel createFromParcel(Parcel in) {
            return new PemesananModel(in);
        }

        @Override
        public PemesananModel[] newArray(int size) {
            return new PemesananModel[size];
        }
    };

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPelaksanaan() {
        return pelaksanaan;
    }

    public void setPelaksanaan(String pelaksanaan) {
        this.pelaksanaan = pelaksanaan;
    }

    public String getPeriasName() {
        return periasName;
    }

    public void setPeriasName(String periasName) {
        this.periasName = periasName;
    }

    public String getPeriasId() {
        return periasId;
    }

    public void setPeriasId(String periasId) {
        this.periasId = periasId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaymentProof() {
        return paymentProof;
    }

    public void setPaymentProof(String paymentProof) {
        this.paymentProof = paymentProof;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
