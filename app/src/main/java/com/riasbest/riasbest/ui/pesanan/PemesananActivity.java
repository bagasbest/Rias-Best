package com.riasbest.riasbest.ui.pesanan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.ActivityPemesananBinding;
import com.riasbest.riasbest.ui.perias.PeriasModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PemesananActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_MODEL = "model";
    private PeriasModel model;
    private ActivityPemesananBinding binding;
    private String category;
    private String dateTime;
    private final ArrayList<String> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPemesananBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_MODEL);
        binding.periasName.setText(model.getName());
        binding.email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        categoryList.addAll(getIntent().getStringArrayListExtra(EXTRA_CATEGORY));

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // pilih kategori
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.categoryMakeup.setAdapter(adapter);
        binding.categoryMakeup.setOnItemClickListener((adapterView, view, i, l) -> {
            category = binding.categoryMakeup.getText().toString();
        });

        // pilih tanggal pemesanan
        binding.dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateTime();
            }
        });

        // klik pesan
        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });
    }

    private void formValidation() {
        String name = binding.name.getText().toString().trim();
        String price = binding.price.getText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(PemesananActivity.this, "Nama Lengkap tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }  else if (price.isEmpty()) {
            Toast.makeText(PemesananActivity.this, "Nama Lengkap tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (dateTime == null) {
            Toast.makeText(PemesananActivity.this, "Tanggal pemesanan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (category == null) {
            Toast.makeText(PemesananActivity.this, "Kategori tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }


        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String orderId = String.valueOf(System.currentTimeMillis());

        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("customerId", customerId);
        order.put("customerName", name);
        order.put("customerEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        order.put("periasName", model.getName());
        order.put("periasId", model.getUid());
        order.put("price", price);
        order.put("dateTime", dateTime);
        order.put("category", category);
        order.put("status", "Belum Bayar");
        order.put("paymentProof", "");
        order.put("dp", model.getDp());

        FirebaseFirestore
                .getInstance()
                .collection("order")
                .document(orderId)
                .set(order)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            showSuccessDialog();
                        } else {
                            mProgressDialog.dismiss();
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
                .setTitle("Berhasil Melakukan Pemesanan")
                .setMessage("Selamat, anda berhasil melakukan pemesanan\n\nSelanjutnya silahkan cek navigasi pemesanan dan unggah bukti pembayaran, Terima kasih")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    private void getDateTime() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()).build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            dateTime = sdf.format(new Date(Long.parseLong(selection.toString())));
            binding.dateTime.setText(dateTime);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}