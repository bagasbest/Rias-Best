package com.riasbest.riasbest.ui.category;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.riasbest.riasbest.R;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class PeriasCategoryAdapter extends RecyclerView.Adapter<PeriasCategoryAdapter.ViewHolder> {

    private final ArrayList<PeriasCategoryModel> listPerias = new ArrayList<>();
    private final ArrayList<String> categoryList;
    final String role;
    public PeriasCategoryAdapter(String role, ArrayList<String> categoryList) {
        this.role = role;
        this.categoryList = categoryList;
    }

    public void setData(ArrayList<PeriasCategoryModel> items) {
        listPerias.clear();
        listPerias.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(role.equals("Perias")) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_perias, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listPerias.get(position), role, categoryList);
    }

    @Override
    public int getItemCount() {
        return listPerias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        ImageView dp;
        TextView category, price;
        View edit, delete;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.roundedImageView);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.price);
            edit = itemView.findViewById(R.id.editView);
            delete = itemView.findViewById(R.id.view2);
        }

        @SuppressLint("SetTextI18n")
        public void bind(PeriasCategoryModel model, String role, ArrayList<String> categoryList) {

            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);

            category.setText(model.getCategory());


            if(role.equals("Pelanggan")) {
                categoryList.add(model.getCategory());
                NumberFormat formatter = new DecimalFormat("#,###");
                price.setText("Rp. " + formatter.format(Double.parseDouble(model.getPrice())));
            } else {
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(itemView.getContext(), PeriasEditCategoryActivity.class);
                        intent.putExtra(PeriasEditCategoryActivity.EXTRA_EDIT, model);
                        itemView.getContext().startActivity(intent);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }


        }
    }
}
