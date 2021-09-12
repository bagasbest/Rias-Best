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

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.ActivityPeriasEditCategoryBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PeriasEditCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT = "edit";
    private ActivityPeriasEditCategoryBinding binding;
    private PeriasCategoryModel model;
    private String category;
    private String image;
    private static final int REQUEST_FROM_GALLERY_TO_SELF_PHOTO = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPeriasEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_EDIT);
        binding.name.setText(model.getName());
        binding.categoryMakeup.setText(model.getCategory());
        binding.price.setText(model.getPrice());

        Glide.with(this)
                .load(model.getDp())
                .into(binding.roundedImageView2);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // edit
        binding.view4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });

        // choose image
        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker
                        .with(PeriasEditCategoryActivity.this)
                        .galleryOnly()
                        .compress(1024)
                        .start(REQUEST_FROM_GALLERY_TO_SELF_PHOTO);
            }
        });

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

    private void validateForm() {
        String name = binding.name.getText().toString().trim();
        String price = binding.price.getText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(PeriasEditCategoryActivity.this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if(price.isEmpty()) {
            Toast.makeText(PeriasEditCategoryActivity.this, "Harga tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar3.setVisibility(View.VISIBLE);


        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        if(category != null) {
            data.put("category", category);
        }
        if(image != null) {
            data.put("dp", image);
        }


        FirebaseFirestore
                .getInstance()
                .collection("perias")
                .document(model.getUid())
                .update(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            binding.progressBar3.setVisibility(View.GONE);
                            showSuccessDialog();
                        } else {
                            binding.progressBar3.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });

    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Mengedit Kategori")
                .setMessage("Mohon maaf, anda gagal mengedit kategori baru, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Mengedit Kategori")
                .setMessage("Selamat, anda berhasil mengedit kategori perias baru pada jasa anda")
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
                                    Glide.with(PeriasEditCategoryActivity.this)
                                            .load(image)
                                            .into(binding.roundedImageView2);
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(PeriasEditCategoryActivity.this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("userDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(PeriasEditCategoryActivity.this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show();
                    Log.d("userDp: ", e.toString());
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}