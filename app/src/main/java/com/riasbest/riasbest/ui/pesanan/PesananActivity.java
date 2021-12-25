package com.riasbest.riasbest.ui.pesanan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPesananBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        model = getIntent().getParcelableExtra(EXTRA_ORDER);

        // ambil alamat perias
        getAddress();

        // get role
        checkRole();


        NumberFormat formatter = new DecimalFormat("#,###");
        binding.price.setText("Rp. " + formatter.format(Double.parseDouble(model.getPrice())));
        binding.nameEt.setText(model.getPeriasName());
        binding.category.setText(model.getCategory());


        // cek apakah sudah bayar atau belum
        if (model.getStatus().equals("Sudah Bayar")) {
            binding.textView15.setText("Daftar Pesanan\nyang Sudah Dibayar");
            binding.textView6.setVisibility(View.VISIBLE);
            binding.paymentProof.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(model.getPaymentProof())
                    .into(binding.paymentProof);
        } else if(model.getStatus().equals("Sedang Dikerjakan")) {
            binding.finishBtn.setVisibility(View.VISIBLE);
            binding.textView15.setText("Daftar Pesanan\nyang Sedang Dikerjakan");
            binding.textView6.setVisibility(View.VISIBLE);
            binding.paymentProof.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(model.getPaymentProof())
                    .into(binding.paymentProof);
        }
        else {
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

        // highlight bukti pembayaran
        binding.paymentProof.setOnClickListener(view -> {
            Dialog dialog;
            Button btnDismiss;
            ImageView dp;

            dialog = new Dialog(PesananActivity.this);

            dialog.setContentView(R.layout.popup_payment_proof);
            dialog.setCanceledOnTouchOutside(false);

            dp = dialog.findViewById(R.id.dp);

            Glide.with(PesananActivity.this)
                    .load(model.getPaymentProof())
                    .into(dp);

            btnDismiss = dialog.findViewById(R.id.dismissBtn);

            btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        });


        binding.beginWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(PesananActivity.this)
                        .setTitle("Konfirmasi Memulai Pengerjaan")
                        .setMessage("Apakah kamu yakin ingin memulai pengerjaan orderan ini ?")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("YA", (dialogInterface, i) -> {

                            ProgressDialog mProgressDialog = new ProgressDialog(PesananActivity.this);

                            mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.show();

                            FirebaseFirestore
                                    .getInstance()
                                    .collection("order")
                                    .document(model.getOrderId())
                                    .update("status", "Sedang Dikerjakan")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialogInterface.dismiss();
                                                mProgressDialog.dismiss();
                                                binding.beginWork.setVisibility(View.GONE);
                                                Toast.makeText(PesananActivity.this, "Anda mulai mengerjakan orderan ini!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialogInterface.dismiss();
                                                mProgressDialog.dismiss();
                                                Toast.makeText(PesananActivity.this, "Gagal memulai pengerjaan orderan ini, silahkan cek koneksi internet anda", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        })
                        .setNegativeButton("TIDAK", null)
                        .show();
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
                        if (task.isSuccessful()) {
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
                .addOnSuccessListener(documentSnapshot -> binding.addressEt.setText("" + documentSnapshot.get("address")));
    }


    private void checkRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (("" + documentSnapshot.get("role")).equals("Perias") && model.getStatus().equals("Sudah Bayar")) {
                            binding.beginWork.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}