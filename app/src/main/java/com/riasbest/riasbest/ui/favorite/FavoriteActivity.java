package com.riasbest.riasbest.ui.favorite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.ActivityFavoriteBinding;
import com.riasbest.riasbest.ui.perias.PeriasAdapter;
import com.riasbest.riasbest.ui.perias.PeriasViewModel;

public class FavoriteActivity extends AppCompatActivity {

    private ActivityFavoriteBinding binding;
    private PeriasAdapter customerAdapter;


    @Override
    protected void onResume() {
        super.onResume();
        initRecylerViewCustomer();
        initViewModelCustomer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initRecylerViewCustomer() {
        // tampilkan perias dalam bentuk list
        binding.rvPerias.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        customerAdapter = new PeriasAdapter();
        binding.rvPerias.setAdapter(customerAdapter);
    }

    private void initViewModelCustomer() {
        // tampilkan daftar perias wajah dari sisi kustomer
        PeriasViewModel viewModel = new ViewModelProvider(this).get(PeriasViewModel.class);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setPeriasListByFavorite(uid);
        viewModel.getPeriasList().observe(this, periasModels -> {
            if (periasModels.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.GONE);
                customerAdapter.setData(periasModels);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}