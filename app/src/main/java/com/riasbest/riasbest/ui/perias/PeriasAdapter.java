package com.riasbest.riasbest.ui.perias;

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
import com.riasbest.riasbest.ui.category.PeriasCategoryActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PeriasAdapter extends RecyclerView.Adapter<PeriasAdapter.ViewHolder> {

    private final ArrayList<PeriasModel> listPerias = new ArrayList<>();
    public void setData(ArrayList<PeriasModel> items) {
        listPerias.clear();
        listPerias.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perias, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listPerias.get(position));
    }

    @Override
    public int getItemCount() {
        return listPerias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        ImageView dp;
        TextView name;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.roundedImageView);
            name = itemView.findViewById(R.id.name);

        }

        public void bind(PeriasModel model) {
            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);

            name.setText(model.getName());

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), PeriasCategoryActivity.class);
                    intent.putExtra(PeriasCategoryActivity.EXTRA_PERIAS, model);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
