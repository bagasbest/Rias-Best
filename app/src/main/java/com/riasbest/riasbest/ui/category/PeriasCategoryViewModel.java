package com.riasbest.riasbest.ui.category;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PeriasCategoryViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<PeriasCategoryModel>> listPerias = new MutableLiveData<>();
    final ArrayList<PeriasCategoryModel> periasCategoryModelArrayList = new ArrayList<>();
    private static final String TAG = PeriasCategoryViewModel.class.getSimpleName();

    public void setPeriasList(String periasId) {
        periasCategoryModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("perias")
                    .whereEqualTo("periasId", periasId)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PeriasCategoryModel perias = new PeriasCategoryModel();
                                perias.setName("" + document.get("name"));
                                perias.setCategory("" + document.get("category"));
                                perias.setDp("" + document.get("dp"));
                                perias.setPeriasId("" + document.get("periasId"));
                                perias.setPrice("" + document.get("price"));
                                perias.setUid("" + document.get("uid"));

                                periasCategoryModelArrayList.add(perias);
                            }
                            listPerias.postValue(periasCategoryModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public LiveData<ArrayList<PeriasCategoryModel>> getPeriasList() {
        return listPerias;
    }


}
