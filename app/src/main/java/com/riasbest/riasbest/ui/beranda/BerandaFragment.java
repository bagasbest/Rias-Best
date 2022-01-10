package com.riasbest.riasbest.ui.beranda;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.riasbest.riasbest.ui.favorite.FavoriteActivity;
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
        // cek apakah pengguna yang login ini merupakan perias atau pelanggan
        checkRole();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBerandaBinding.inflate(inflater, container, false);

        // cek user id
        user = FirebaseAuth.getInstance().getCurrentUser();


        // tampilkan banner perias
        Glide.with(getActivity())
                .load(R.drawable.banner)
                .into(binding.roundedImageView);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // tambah kategori perias
        binding.view4.setOnClickListener(view1 -> {
            Intent intent = new Intent (getActivity(), PeriasAddCategoryActivity.class);
            intent.putExtra(PeriasAddCategoryActivity.EXTRA_NAME, name);
            startActivity(intent);
        });

        // cari perias
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    initRecylerViewCustomer();
                    initViewModelCustomer(editable.toString());
                } else {
                    initRecylerViewCustomer();
                    initViewModelCustomer("all");
                }
            }
        });

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FavoriteActivity.class));
            }
        });

    }

    private void checkRole () {

        // cek role apakah pelanggan / perias
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
                            initViewModelCustomer("all");
                            binding.favorite.setVisibility(View.VISIBLE);
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)binding.searchEt.getLayoutParams();
                            layoutParams.setMarginEnd(250);
                            binding.searchEt.setLayoutParams(layoutParams);
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
        // tampilkan perias dalam bentuk list
        binding.rvPerias.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        customerAdapter = new PeriasAdapter();
        binding.rvPerias.setAdapter(customerAdapter);
    }

    private void initViewModelCustomer(String query) {
        // tampilkan daftar perias wajah dari sisi kustomer
        PeriasViewModel viewModel = new ViewModelProvider(this).get(PeriasViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(query.equals("all")) {
            viewModel.setPeriasList();
        } else {
            viewModel.setPeriasListByQuery(query);
        }
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
        // tampilkan jasa perias dalam bentuk list
        binding.rvPeriasRole.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        categoryAdapter = new PeriasCategoryAdapter("Perias", null);
        binding.rvPeriasRole.setAdapter(categoryAdapter);
    }

    private void initViewModelPerias() {
        // tampilkan daftar jasa perias dari sisi perias
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

    // destroy activity jika tidak digunakan
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}