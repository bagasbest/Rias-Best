package com.riasbest.riasbest;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riasbest.riasbest.databinding.ActivityRegisterBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String levelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(view -> onBackPressed());

        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInput();
            }
        });

        // pilih level user: Perias atau Pelanggan
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.level_user, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.levelUser.setAdapter(adapter);
        binding.levelUser.setOnItemClickListener((adapterView, view, i, l) -> {
            levelUser = binding.levelUser.getText().toString();
        });


    }

    private void getUserInput() {
        String name = binding.nameEt.getText().toString().trim();
        String email = binding.emailEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();
        String username = binding.usernameEt.getText().toString().trim();
        String cfPassword = binding.confirmPassword.getText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if(email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if(!email.contains("@") || !email.contains(".")) {
            Toast.makeText(RegisterActivity.this, "Format email tidak sesuai!", Toast.LENGTH_SHORT).show();
            return;
        } else if(password.isEmpty() || cfPassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if(!password.equals(cfPassword)) {
            Toast.makeText(RegisterActivity.this, "Password & Konfirmasi Password tidak sama!", Toast.LENGTH_SHORT).show();
            return;
        } else if(username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Username tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (levelUser == null) {
            Toast.makeText(RegisterActivity.this, "level user tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        // registrasi pengguna baru
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // save biodata to firestore
                            saveBiodata(mProgressDialog, name, email, password, username);
                        } else {
                            mProgressDialog.dismiss();
                            showFailureDialog();
                        }
                    }
                });
    }

    private void saveBiodata(ProgressDialog mProgressDialog, String name, String email, String password, String username) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> register = new HashMap<>();
        register.put("name", name);
        register.put("username", username);
        register.put("email", email);
        register.put("password", password);
        register.put("uid", uid);
        register.put("role", levelUser);
        register.put("address", "");
        register.put("dp", "");

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .set(register)
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
                .setTitle("Gagal melakukan registrasi")
                .setMessage("Silahkan mendaftar kembali dengan informasi yang benar")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil melakukan registrasi")
                .setMessage("Silahkan login")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
