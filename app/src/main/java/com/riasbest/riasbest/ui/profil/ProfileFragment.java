package com.riasbest.riasbest.ui.profil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riasbest.riasbest.LoginActivity;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.FragmentProfileBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseUser user;

    // variabel
    private static final int REQUEST_FROM_GALLERY_TO_SELF_PHOTO = 1001;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // AMBIL SEMUA DATA DARI DATABASE DAN TAMPILKAN
        populateUI();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // LOGOUT DARI APLIKASI
        binding.logoutBtn.setOnClickListener(view1 -> showLogoutDialog());

        // EDIT PROFIL
        binding.editBtn.setOnClickListener(view12 -> editProfile());

        // GANTI FOTO PROFIL
        binding.dp.setOnClickListener(view13 -> updateUserDp());

    }

    private void populateUI() {
        // AMBIL DATA PENGGUNA DARI DATABASE, UNTUK DITAMPILKAN SEBAGAI PROFIL
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String name = "" + documentSnapshot.get("name");
                    String email = "" + documentSnapshot.get("email");
                    String address = "" + documentSnapshot.get("address");
                    String userDp = "" + documentSnapshot.get("dp");

                    //TERAPKAN PADA UI PROFIL
                    binding.nameEt.setText(name);
                    binding.emailEt.setText(email);
                    binding.addressEt.setText(address);

                    Glide.with(this)
                            .load(userDp)
                            .into(binding.dp);

                    if(!userDp.isEmpty()) {
                        binding.hintIv.setVisibility(View.GONE);
                    }

                    binding.emailEt.setEnabled(false);

                })
                .addOnFailureListener(e -> {
                    Log.e("Error get profil", e.toString());
                    Toast.makeText(requireContext(), "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserDp() {
            ImagePicker
                    .with(this)
                    .galleryOnly()
                    .compress(1024)
                    .start(REQUEST_FROM_GALLERY_TO_SELF_PHOTO);
    }

    private void editProfile() {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi Perbarui Profil")
                    .setMessage("Apakah kamu yakin ingin memperbarui profil, berdasarkan data yang telah kamu inputkan ?")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("YA", (dialogInterface, i) -> {
                        // SIMPAN PERUBAHAN PROFIL PENGGUNA KE DATABASE
                        saveProfileChangesToDatabase();
                    })
                    .setNegativeButton("TIDAK", null)
                    .show();
    }

    private void saveProfileChangesToDatabase() {
        String name = binding.nameEt.getText().toString().trim();
        String address = binding.addressEt.getText().toString().trim();

        // VALIDASI KOLOM PROFIL, JANGAN SAMPAI ADA YANG KOSONG
        if (name.isEmpty()) {
            binding.nameEt.setError("Nama Lengkap tidak boleh kosong");
            return;
        } else if (address.isEmpty()) {
            binding.addressEt.setError("Alamat tidak boleh kosong");
            return;
        }

        ProgressDialog mProgressDialog = new ProgressDialog(requireContext());

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        Map<String, Object> updateProfile = new HashMap<>();
        updateProfile.put("name", name);
        updateProfile.put("address", address);

        // SIMPAN PERUBAHAN PROFIL TERBARU KE DATABASE
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .update(updateProfile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgressDialog.dismiss();
                        Toast.makeText(requireContext(), "Berhasil memperbarui profil", Toast.LENGTH_SHORT).show();
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(requireContext(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah anda yakin ingin keluar apliaksi ?")
                .setIcon(R.drawable.ic_baseline_exit_to_app_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    // sign out dari firebase autentikasi
                    FirebaseAuth.getInstance().signOut();

                    // go to login activity
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogInterface.dismiss();
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY_TO_SELF_PHOTO) {
                ProfileDatabase.uploadImageToDatabase(data.getData(), getActivity(), user.getUid());
                Glide.with(this)
                        .load(data.getData())
                        .into(binding.dp);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}