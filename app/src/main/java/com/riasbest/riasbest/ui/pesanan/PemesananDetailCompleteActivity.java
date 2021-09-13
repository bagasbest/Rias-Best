package com.riasbest.riasbest.ui.pesanan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.ActivityPemesananDetailCompleteBinding;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PemesananDetailCompleteActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER = "order";
    private ActivityPemesananDetailCompleteBinding binding;
    private PemesananModel model;
    private String address;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPemesananDetailCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        model = getIntent().getParcelableExtra(EXTRA_ORDER);

        // ambil alamat perias
        getAddress();

        binding.nameEt.setText(model.getPeriasName());
        binding.category.setText(model.getCategory());

        NumberFormat formatter = new DecimalFormat("#,###");
        binding.price.setText("Rp. " + formatter.format(Double.parseDouble(model.getPrice())));

        if(model.getStatus().equals("Sudah Bayar")) {
            binding.finishBtn.setVisibility(View.VISIBLE);
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertFinish();
            }
        });

    }

    private void showAlertFinish() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Menyelesaikan Jasa")
                .setMessage("Apakah kamu yakin ingin menyelesaikan Jasa dari Perias ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    // selesaikan
                    finishOrder();
                })
                .setNegativeButton("TIDAK", null)
                .show();
    }

    private void finishOrder() {
        FirebaseFirestore
                .getInstance()
                .collection("order")
                .document(model.getOrderId())
                .update("status", "Selesai")
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
                .setTitle("Gagal Menyelesaikan Jasa")
                .setMessage("Mohon maaf, anda gagal menyelesaikan jasa perias ini, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Meyelesaikan Jasa")
                .setMessage("Selamat, anda berhasil menyelesaikan jasa perias ini.\n\nTerima kasih atas kepercayaan anda menggunakan jasa kami.")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    binding.finishBtn.setVisibility(View.GONE);
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    private void getAddress() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(model.getPeriasId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        address = "" + documentSnapshot.get("address");
                        binding.addressEt.setText(address);

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}