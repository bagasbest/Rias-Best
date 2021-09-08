package com.riasbest.riasbest;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.riasbest.riasbest.databinding.ActivityHomepageBinding;
import com.riasbest.riasbest.ui.beranda.BerandaFragment;
import com.riasbest.riasbest.ui.pesanan.PesananFragment;
import com.riasbest.riasbest.ui.profil.ProfileFragment;

public class HomepageActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.riasbest.riasbest.databinding.ActivityHomepageBinding binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // untuk mengganti halaman contoh: halaman produk -> halaman keranjang -> halaman pemesanan/pembayaran
        navView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = new BerandaFragment();
            switch (item.getItemId()) {
                case R.id.navigation_beranda: {
                    navView.getMenu().findItem(R.id.navigation_beranda).setEnabled(false);
                    navView.getMenu().findItem(R.id.navigation_pesanan).setEnabled(true);
                    navView.getMenu().findItem(R.id.navigation_profile).setEnabled(true);
                    selectedFragment = new BerandaFragment();
                    break;
                }
                case R.id.navigation_pesanan: {
                    navView.getMenu().findItem(R.id.navigation_beranda).setEnabled(false);
                    navView.getMenu().findItem(R.id.navigation_pesanan).setEnabled(true);
                    navView.getMenu().findItem(R.id.navigation_profile).setEnabled(true);
                    selectedFragment = new PesananFragment();
                    break;
                }
                case R.id.navigation_profile: {
                    navView.getMenu().findItem(R.id.navigation_beranda).setEnabled(false);
                    navView.getMenu().findItem(R.id.navigation_pesanan).setEnabled(true);
                    navView.getMenu().findItem(R.id.navigation_profile).setEnabled(true);
                    selectedFragment = new ProfileFragment();
                    break;
                }
                default: {
                    navView.getMenu().findItem(R.id.navigation_beranda).setEnabled(false);
                }
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, selectedFragment);
            transaction.commit();
            return true;
        });
    }
}