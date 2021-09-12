package com.riasbest.riasbest.ui.beranda;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.FragmentBerandaBinding;
import com.riasbest.riasbest.ui.category.PeriasAddCategoryActivity;
import com.riasbest.riasbest.ui.category.PeriasCategoryAdapter;
import com.riasbest.riasbest.ui.category.PeriasCategoryViewModel;
import com.riasbest.riasbest.ui.perias.PeriasAdapter;
import com.riasbest.riasbest.ui.perias.PeriasViewModel;

import org.jetbrains.annotations.NotNull;

public class BerandaFragment extends Fragment {

    private FragmentBerandaBinding binding;
    private FirebaseUser user;
    private String name;
    private PeriasAdapter customerAdapter;
    private PeriasCategoryAdapter categoryAdapter;

    @Override
    public void onResume() {
        super.onResume();
        checkRole();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBerandaBinding.inflate(inflater, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();


        Glide.with(getActivity())
                .load(R.drawable.banner)
                .into(binding.roundedImageView);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.view4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getActivity(), PeriasAddCategoryActivity.class);
                intent.putExtra(PeriasAddCategoryActivity.EXTRA_NAME, name);
                startActivity(intent);
            }
        });

    }

    private void checkRole () {

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(("" + documentSnapshot.get("role")).equals("Pelanggan")) {
                            binding.customerRole.setVisibility(View.VISIBLE);
                            initRecylerViewCustomer();
                            initViewModelCustomer();
                        } else {
                            binding.periasRole.setVisibility(View.VISIBLE);
                            initRecylerViewPerias();
                            initViewModelPerias();
                        }
                        name = "" + documentSnapshot.get("name");
                    }
                });
    }

    private void initRecylerViewCustomer() {
        binding.rvPerias.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        customerAdapter = new PeriasAdapter();
        binding.rvPerias.setAdapter(customerAdapter);
    }

    private void initViewModelCustomer() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        PeriasViewModel viewModel = new ViewModelProvider(this).get(PeriasViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setPeriasList();
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

    private void initRecylerViewPerias() {
        binding.rvPeriasRole.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        categoryAdapter = new PeriasCategoryAdapter("Perias", null);
        binding.rvPeriasRole.setAdapter(categoryAdapter);
    }

    private void initViewModelPerias() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        PeriasCategoryViewModel viewModel = new ViewModelProvider(this).get(PeriasCategoryViewModel.class);

        binding.progressBarPerias.setVisibility(View.VISIBLE);
        viewModel.setPeriasList(user.getUid());
        viewModel.getPeriasList().observe(this, periasModels -> {
            if (periasModels.size() > 0) {
                binding.progressBarPerias.setVisibility(View.GONE);
                binding.noDataPerias.setVisibility(View.GONE);
                categoryAdapter.setData(periasModels);
            } else {
                binding.progressBarPerias.setVisibility(View.GONE);
                binding.noDataPerias.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}