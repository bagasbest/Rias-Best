package com.riasbest.riasbest.ui.pesanan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.ActivityPesananBinding;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PesananActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER = "order";
    private ActivityPesananBinding binding;
    private PemesananModel model;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPesananBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        model = getIntent().getParcelableExtra(EXTRA_ORDER);

        // ambil alamat perias
        getAddress();


        NumberFormat formatter = new DecimalFormat("#,###");
        binding.price.setText("Rp. " + formatter.format(Double.parseDouble(model.getPrice())));
        binding.nameEt.setText(model.getPeriasName());
        binding.category.setText(model.getCategory());
        binding.addressEt.setText(address);


        // cek apakah sudah bayar atau belum
        if(model.getStatus().equals("Sudah Bayar") || model.getStatus().equals("Selesai")) {
            binding.textView15.setText("Daftar Pesanan\nyang Sudah Dibayar");
            binding.textView6.setVisibility(View.VISIBLE);
            binding.paymentProof.setVisibility(View.VISIBLE);
            if(model.getStatus().equals("Selesai")) {
                binding.finishBtn.setVisibility(View.VISIBLE);
            }
            Glide.with(this)
                    .load(model.getPaymentProof())
                    .into(binding.paymentProof);
        } else {
            binding.textView15.setText("Daftar Pesanan");
        }

        // kembali
        binding.backButton.setOnClickListener(view -> onBackPressed());

        // finish
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
                .setMessage("Apakah kamu yakin ingin menyelesaikan pemesanan dari kustomer ini ?")
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
                .setMessage("Mohon maaf, anda gagal menyelesaikan jasa dari kustomer ini, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Meyelesaikan Jasa")
                .setMessage("Selamat, anda berhasil menyelesaikan jasa dari kustomer ini.\n\nTerima kasih.")
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
                .addOnSuccessListener(documentSnapshot -> address = ""+documentSnapshot.get("address"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}