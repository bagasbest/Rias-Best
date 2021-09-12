package com.riasbest.riasbest.ui.pesanan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riasbest.riasbest.R;
import com.riasbest.riasbest.databinding.FragmentPesananBinding;

public class PesananFragment extends Fragment {

    private FragmentPesananBinding binding;
    private PemesananAdapter adapter;
    private String status = "all";
    private FirebaseUser user;
    private String role;

    @Override
    public void onResume() {
        super.onResume();
        checkRole();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPesananBinding.inflate(inflater, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();

        // pilih kategori
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.status, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.paymentStatus.setAdapter(adapter);
        binding.paymentStatus.setOnItemClickListener((adapterView, view, i, l) -> {
            status = binding.paymentStatus.getText().toString();
            initRecyclerView();
            initViewModel();
        });


        return binding.getRoot();
    }

    private void checkRole() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    role = ""+documentSnapshot.get("role");
                    initRecyclerView();
                    initViewModel();
                });
    }

    private void initRecyclerView() {
        binding.rvPemesananCustomer.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PemesananAdapter(role);
        binding.rvPemesananCustomer.setAdapter(adapter);
    }

    private void initViewModel() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        PemesananViewModel viewModel = new ViewModelProvider(this).get(PemesananViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(status.equals("all")){
            viewModel.setListOrderAll(user.getUid());
        } else {
            viewModel.setListOrderByStatus(user.getUid(), status);
        }
        viewModel.getOrder().observe(getViewLifecycleOwner(), order -> {
            if (order.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.GONE);
                adapter.setData(order);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}