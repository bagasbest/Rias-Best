package com.riasbest.riasbest.ui.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.ActivityPeriasAddCategoryBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PeriasAddCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "name";
    private ActivityPeriasAddCategoryBinding binding;
    private String category;
    private String image;
    private static final int REQUEST_FROM_GALLERY_TO_SELF_PHOTO = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPeriasAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // set nama perias
        binding.name.setText(getIntent().getStringExtra(EXTRA_NAME));

        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(view -> onBackPressed());

        // submit data
        binding.view2.setOnClickListener(view -> addCategory());

        // ambil gambar dari galeri
        binding.view6.setOnClickListener(view -> pickImage());

        // pilih kategori
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_makeup, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.categoryMakeup.setAdapter(adapter);
        binding.categoryMakeup.setOnItemClickListener((adapterView, view, i, l) -> {
            category = binding.categoryMakeup.getText().toString();
        });

    }

    private void pickImage() {
        ImagePicker
                .with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY_TO_SELF_PHOTO);
    }

    private void addCategory() {
        String name = binding.name.getText().toString().trim();
        String price = binding.price.getText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(PeriasAddCategoryActivity.this, "Nama Perias tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (price.isEmpty()) {
            Toast.makeText(PeriasAddCategoryActivity.this, "Harga tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (category == null) {
            Toast.makeText(PeriasAddCategoryActivity.this, "Kategori tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (image == null) {
            Toast.makeText(PeriasAddCategoryActivity.this, "Gambar tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        String periasId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String uid = String.valueOf(System.currentTimeMillis());

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("category", category);
        data.put("dp", image);
        data.put("periasId", periasId);
        data.put("uid", uid);


        FirebaseFirestore
                .getInstance()
                .collection("perias")
                .document(uid)
                .set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            showSuccessDialog();
                        } else {
                            showFailureDialog();
                        }
                    }
                });

    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Menambah Kategori")
                .setMessage("Mohon maaf, anda gagal menambah kategori baru, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Menambah Kategori")
                .setMessage("Selamat, anda berhasil menambah kategori perias baru pada jasa anda")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY_TO_SELF_PHOTO) {
                uploadImageToDatabase(data.getData());
            }
        }
    }

    private void uploadImageToDatabase(Uri data) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "perias/" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    image = uri.toString();
                                    mProgressDialog.dismiss();
                                    binding.textView10.setText("Berhasil Ditambahkan");
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(PeriasAddCategoryActivity.this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("userDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(PeriasAddCategoryActivity.this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show();
                    Log.d("userDp: ", e.toString());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}