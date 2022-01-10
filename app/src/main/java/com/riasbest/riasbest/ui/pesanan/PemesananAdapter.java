package com.riasbest.riasbest.ui.pesanan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.riasbest.riasbest.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PemesananAdapter extends RecyclerView.Adapter<PemesananAdapter.ViewHolder> {

    private final ArrayList<PemesananModel> listPemesanan = new ArrayList<>();

    private final String role;

    public PemesananAdapter(String role) {
        this.role = role;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<PemesananModel> items) {
        listPemesanan.clear();
        listPemesanan.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pemesanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listPemesanan.get(position), role);
    }

    @Override
    public int getItemCount() {
        return listPemesanan.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        ImageView dp;
        View view;
        TextView name, category, dateTime, status, pelaksanaan;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.dp);
            view = itemView.findViewById(R.id.view7);
            name = itemView.findViewById(R.id.periasName);
            category = itemView.findViewById(R.id.category);
            dateTime = itemView.findViewById(R.id.dateTime);
            pelaksanaan = itemView.findViewById(R.id.pelaksanaan);
            status = itemView.findViewById(R.id.status);

        }

        @SuppressLint("SetTextI18n")
        public void bind(PemesananModel model, String role) {
            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);

            name.setText("Nama Perias: " + model.getPeriasName());
            category.setText("Kategori: " + model.getCategory());
            dateTime.setText("Pemesanan: " + model.getDateTime());
            if (!model.getPelaksanaan().equals("")) {
                pelaksanaan.setText("Pelaksanaan: " + model.getPelaksanaan());
            } else {
                pelaksanaan.setText("Pelaksanaan: -");
            }
            status.setText(model.getStatus());

            if (role.equals("Pelanggan")) {

                switch (model.getStatus()) {
                    case "Belum Bayar":
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_red_dark));
                        cv.setOnClickListener(view -> {
                            Intent intent = new Intent(itemView.getContext(), PemesananDetailActivity.class);
                            intent.putExtra(PemesananDetailActivity.EXTRA_ORDER, model);
                            itemView.getContext().startActivity(intent);
                        });
                        break;

                    case "Menunggu":
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_purple));
                        cv.setOnClickListener(view -> {
                            Intent intent = new Intent(itemView.getContext(), PemesananDetailActivity.class);
                            intent.putExtra(PemesananDetailActivity.EXTRA_ORDER, model);
                            itemView.getContext().startActivity(intent);
                        });
                        break;
                    case "Sudah Bayar":
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_orange_dark));
                        cv.setOnClickListener(view -> {
                            Intent intent = new Intent(itemView.getContext(), PemesananDetailCompleteActivity.class);
                            intent.putExtra(PemesananDetailCompleteActivity.EXTRA_ORDER, model);
                            itemView.getContext().startActivity(intent);
                        });
                        break;
                    case "Sedang Dikerjakan":
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_blue_dark));
                        cv.setOnClickListener(view -> {
                            Intent intent = new Intent(itemView.getContext(), PemesananDetailCompleteActivity.class);
                            intent.putExtra(PemesananDetailCompleteActivity.EXTRA_ORDER, model);
                            itemView.getContext().startActivity(intent);
                        });
                        break;
                    default:
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_green_dark));
                        cv.setOnClickListener(view -> {
                            Intent intent = new Intent(itemView.getContext(), PemesananDetailCompleteActivity.class);
                            intent.putExtra(PemesananDetailCompleteActivity.EXTRA_ORDER, model);
                            itemView.getContext().startActivity(intent);
                        });
                        break;
                }

            } else {

                switch (model.getStatus()) {
                    case "Belum Bayar":
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_red_dark));
                        break;
                    case "Menunggu":
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_purple));
                        break;
                    case "Sudah Bayar":
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_orange_dark));
                        break;
                    case "Sedang Dikerjakan":
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_blue_dark));
                        break;
                    default:
                        view.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_green_dark));
                        break;
                }

                cv.setOnClickListener(view -> {
                    Intent intent = new Intent(itemView.getContext(), PesananActivity.class);
                    intent.putExtra(PesananActivity.EXTRA_ORDER, model);
                    itemView.getContext().startActivity(intent);
                });
            }
        }
    }
}
