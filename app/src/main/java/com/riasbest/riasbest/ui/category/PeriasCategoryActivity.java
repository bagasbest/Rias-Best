package com.riasbest.riasbest.ui.category;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.ActivityPeriasCategoryBinding;
import com.riasbest.riasbest.ui.perias.PeriasModel;
import com.riasbest.riasbest.ui.pesanan.PemesananActivity;

import java.util.ArrayList;

public class PeriasCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_PERIAS = "perias";
    private ActivityPeriasCategoryBinding binding;
    private PeriasCategoryAdapter categoryAdapter;
    private FirebaseUser user;
    private PeriasModel model;
    private ArrayList<String> category = new ArrayList<>();
    private ArrayList<String> favoritedBy = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPeriasCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        user = FirebaseAuth.getInstance().getCurrentUser();

        model = getIntent().getParcelableExtra(EXTRA_PERIAS);
        checkIsFavoritePeriasOrNot();

        binding.name.setText(model.getName());
        binding.nameEt.setText(model.getName());
        binding.addressEt.setText(model.getAddress());
        binding.rekening.setText(model.getRekening());
        if(model.getFavoritedBy() != null) {
            favoritedBy.addAll(model.getFavoritedBy());
        }

        initRecylerViewPerias();
        initViewModelPerias();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PeriasCategoryActivity.this, PemesananActivity.class);
                intent.putExtra(PemesananActivity.EXTRA_CATEGORY, category);
                intent.putExtra(PemesananActivity.EXTRA_MODEL, model);
                startActivity(intent);
            }
        });

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavorite = sharedPreferences.getBoolean(model.getUid(), false);
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (isFavorite) {
                    for(int i=0; i<favoritedBy.size(); i++) {
                        if(favoritedBy.get(i).equals(myUid)) {
                            favoritedBy.remove(i);
                            break;
                        }
                    }
                    FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(model.getUid())
                            .update("favoritedBy", favoritedBy);

                    binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putBoolean(model.getUid(), false);
                    myEdit.apply();
                    Toast.makeText(PeriasCategoryActivity.this, "Berhasil menghapus perias dari daftar favorit", Toast.LENGTH_SHORT).show();
                } else {

                    favoritedBy.add(myUid);
                    FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(model.getUid())
                            .update("favoritedBy", favoritedBy);

                    binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putBoolean(model.getUid(), true);
                    myEdit.apply();
                    Toast.makeText(PeriasCategoryActivity.this, "Berhasil menambahkan perias kedalam daftar favorit", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkIsFavoritePeriasOrNot() {
        boolean isFavorite = sharedPreferences.getBoolean(model.getUid(), false);

        if (isFavorite) {
            binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

    }

    private void initRecylerViewPerias() {
        binding.rvPeriasRole.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        categoryAdapter = new PeriasCategoryAdapter("Pelanggan", category);
        binding.rvPeriasRole.setAdapter(categoryAdapter);
    }

    private void initViewModelPerias() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        PeriasCategoryViewModel viewModel = new ViewModelProvider(this).get(PeriasCategoryViewModel.class);

        binding.progressBarPerias.setVisibility(View.VISIBLE);
        viewModel.setPeriasList(model.getUid());
        viewModel.getPeriasList().observe(this, periasModels -> {
            Log.e("TAG", String.valueOf(periasModels.size()));
            if (periasModels.size() > 0) {
                binding.progressBarPerias.setVisibility(View.GONE);
                categoryAdapter.setData(periasModels);
            } else {
                binding.progressBarPerias.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}