package com.riasbest.riasbest.ui.pesanan;

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
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.ActivityPemesananDetailBinding;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class PemesananDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER = "order";
    private ActivityPemesananDetailBinding binding;
    private PemesananModel model;
    private String address;
    private String paymentProof;
    private static final int REQUEST_FROM_GALLERY_TO_SELF_PHOTO = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPemesananDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_ORDER);

        // ambil alamat perias
        getAddress();

        binding.nameEt.setText(model.getPeriasName());
        binding.category.setText(model.getCategory());

        NumberFormat formatter = new DecimalFormat("#,###");
        binding.price.setText("Rp. " + formatter.format(Double.parseDouble(model.getPrice())));

        // cek apakah sudah upload bukti pembayaran atau belum
        if(model.getPaymentProof().equals("")) {
            binding.paymentProofBtn.setVisibility(View.VISIBLE);
            binding.buttons.setVisibility(View.VISIBLE);
        } else {
            binding.paymentProof.setVisibility(View.VISIBLE);
        }

        // kembali
        binding.backButton.setOnClickListener(view -> onBackPressed());


        // batalkan pemesanan
        binding.cancelOrder.setOnClickListener(view -> showDialogCancelOrder());

        // kirim bukti pembayaran
        binding.view10.setOnClickListener(view -> sendPaymentProof());

        // batalkan kirim bukti pembayaran
        binding.view11.setOnClickListener(view -> {
            if(paymentProof == null) {
                Toast.makeText(PemesananDetailActivity.this, "Ups, tidak ada gambar yang di unggah!", Toast.LENGTH_SHORT).show();
                return;
            }
            paymentProof = null;
            binding.paymentProofBtn.setText("Choose Image");
        });

        // upload bukti pembayaran
        binding.paymentProofBtn.setOnClickListener(view -> ImagePicker
                .with(PemesananDetailActivity.this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY_TO_SELF_PHOTO));
    }

    private void sendPaymentProof() {

        if(paymentProof == null) {
            Toast.makeText(PemesananDetailActivity.this, "Ups, anda harus mengunggah bukti pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> payment = new HashMap<>();
        payment.put("paymentProof", paymentProof);
        payment.put("status", "Sudah Bayar");

        FirebaseFirestore
                .getInstance()
                .collection("order")
                .document(model.getOrderId())
                .update(payment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            showSuccessDialog("Mengirimkan Bukti Pembayaran", "mengirimkan bukti pembayaran, selanjutnya kamu akan menemui");
                        } else {
                            showFailureDialog("Mengirimkan Bukti Pembayaran", "mengirimkan bukti pembayaran");
                        }
                    }
                });
    }

    private void showDialogCancelOrder() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Batalkan Pemesanan")
                .setMessage("Apakah kamu yakin ingin membatalkan pemesanan perias wajah ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    // Batalkan orderan
                    cancelOrder();
                })
                .setNegativeButton("TIDAK", null)
                .show();
    }

    private void cancelOrder() {
        FirebaseFirestore
                .getInstance()
                .collection("order")
                .document(model.getOrderId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            showSuccessDialog("Membatalkan Pesanan", "membatalkan pesanan");
                        } else {
                            showFailureDialog("Membatalkan Pesanan", "membatalkan pesanan");
                        }
                    }
                });
    }

    private void showFailureDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle("Gagal " + title)
                .setMessage("Mohon maaf, anda gagal " + message + " perias wajah ini")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil " + title)
                .setMessage("Selamat, anda berhasil " + message + " perias wajah ini")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
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
                        address = ""+documentSnapshot.get("address");
                        binding.addressEt.setText(address);
                    }
                });
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
        String imageFileName = "payment_proof/" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    paymentProof = uri.toString();
                                    mProgressDialog.dismiss();
                                    binding.paymentProofBtn.setText("Berhasil Ditambahkan");
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(PemesananDetailActivity.this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("userDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(PemesananDetailActivity.this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show();
                    Log.d("userDp: ", e.toString());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}