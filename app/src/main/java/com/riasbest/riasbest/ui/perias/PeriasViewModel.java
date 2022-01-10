package com.riasbest.riasbest.ui.perias;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.riasbest.riasbest.ui.category.PeriasCategoryViewModel;

import java.util.ArrayList;

public class PeriasViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<PeriasModel>> listPerias = new MutableLiveData<>();
    final ArrayList<PeriasModel> periasCategoryModelArrayList = new ArrayList<>();
    private static final String TAG = PeriasCategoryViewModel.class.getSimpleName();

    public void setPeriasList() {
        periasCategoryModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .whereEqualTo("role", "Perias")
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PeriasModel perias = new PeriasModel();
                                perias.setName("" + document.get("name"));
                                perias.setDp("" + document.get("dp"));
                                perias.setEmail("" + document.get("email"));
                                perias.setRole("" + document.get("role"));
                                perias.setAddress("" + document.get("address"));
                                perias.setUsername("" + document.get("username"));
                                perias.setUid("" + document.get("uid"));
                                perias.setFavoritedBy((ArrayList<String>) document.get("favoritedBy"));
                                String noRek = "" + document.get("rekening");
                                if(noRek.equals("null")) {
                                    perias.setRekening("");
                                } else {
                                    perias.setRekening(noRek);
                                }
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

    public void setPeriasListByQuery(String query) {
        periasCategoryModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .whereEqualTo("role", "Perias")
                    .whereGreaterThanOrEqualTo("nameTemp", query)
                    .whereLessThanOrEqualTo("nameTemp", query + '\uf8ff')
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PeriasModel perias = new PeriasModel();
                                perias.setName("" + document.get("name"));
                                perias.setNameTemp("" + document.get("nameTemp"));
                                perias.setDp("" + document.get("dp"));
                                perias.setEmail("" + document.get("email"));
                                perias.setRole("" + document.get("role"));
                                perias.setAddress("" + document.get("address"));
                                perias.setUsername("" + document.get("username"));
                                perias.setUid("" + document.get("uid"));
                                perias.setFavoritedBy((ArrayList<String>) document.get("favoritedBy"));

                                String noRek = "" + document.get("rekening");
                                if(noRek.equals("null")) {
                                    perias.setRekening("");
                                } else {
                                    perias.setRekening(noRek);
                                }
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

    public void setPeriasListByFavorite(String uid) {
        periasCategoryModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .whereEqualTo("role", "Perias")
                    .whereArrayContains("favoritedBy", uid)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PeriasModel perias = new PeriasModel();
                                perias.setName("" + document.get("name"));
                                perias.setDp("" + document.get("dp"));
                                perias.setEmail("" + document.get("email"));
                                perias.setRole("" + document.get("role"));
                                perias.setAddress("" + document.get("address"));
                                perias.setUsername("" + document.get("username"));
                                perias.setUid("" + document.get("uid"));
                                perias.setFavoritedBy((ArrayList<String>) document.get("favoritedBy"));

                                String noRek = "" + document.get("rekening");
                                if(noRek.equals("null")) {
                                    perias.setRekening("");
                                } else {
                                    perias.setRekening(noRek);
                                }
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

    public LiveData<ArrayList<PeriasModel>> getPeriasList() {
        return listPerias;
    }


}
