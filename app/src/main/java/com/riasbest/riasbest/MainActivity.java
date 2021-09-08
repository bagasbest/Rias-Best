package com.riasbest.riasbest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.riasbest.riasbest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this)
                .load(R.drawable.logoo)
                .into(binding.logo);

        // Handler untuk menampilkan splash screen selama 4 detik (4000 mil detik) sebelum masuk ke login / homepage
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }, 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}