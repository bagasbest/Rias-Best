package com.riasbest.riasbest.ui.pesanan;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PemesananViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<PemesananModel>> listPemesanan = new MutableLiveData<>();
    final ArrayList<PemesananModel> pemesananModelArrayList = new ArrayList<>();
    private static final String TAG = PemesananViewModel.class.getSimpleName();

    public void setListOrderAll(String customerId) {
        pemesananModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("order")
                    .whereEqualTo("customerId", customerId)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PemesananModel order = new PemesananModel();
                                order.setCategory("" + document.get("category"));
                                order.setCustomerEmail("" + document.get("customerEmail"));
                                order.setCustomerId("" + document.get("customerId"));
                                order.setCustomerName("" + document.get("customerName"));
                                order.setDateTime("" + document.get("dateTime"));
                                order.setDp("" + document.get("dp"));
                                order.setOrderId("" + document.get("orderId"));
                                order.setPaymentProof("" + document.get("paymentProof"));
                                order.setPeriasId("" + document.get("periasId"));
                                order.setPeriasName("" + document.get("periasName"));
                                order.setPrice("" + document.get("price"));
                                order.setStatus("" + document.get("status"));

                                pemesananModelArrayList.add(order);
                            }
                            listPemesanan.postValue(pemesananModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListOrderByStatus(String customerId, String status) {
        pemesananModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("order")
                    .whereEqualTo("customerId", customerId)
                    .whereEqualTo("status", status)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PemesananModel order = new PemesananModel();
                                order.setCategory("" + document.get("category"));
                                order.setCustomerEmail("" + document.get("customerEmail"));
                                order.setCustomerId("" + document.get("customerId"));
                                order.setCustomerName("" + document.get("customerName"));
                                order.setDateTime("" + document.get("dateTime"));
                                order.setDp("" + document.get("dp"));
                                order.setOrderId("" + document.get("orderId"));
                                order.setPaymentProof("" + document.get("paymentProof"));
                                order.setPeriasId("" + document.get("periasId"));
                                order.setPeriasName("" + document.get("periasName"));
                                order.setPrice("" + document.get("price"));
                                order.setStatus("" + document.get("status"));

                                pemesananModelArrayList.add(order);
                            }
                            listPemesanan.postValue(pemesananModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public LiveData<ArrayList<PemesananModel>> getOrder() {
        return listPemesanan;
    }

}
