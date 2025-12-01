package com.example.zenithchance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;

import java.util.List;

public class AdminImageGridAdapter extends RecyclerView.Adapter<AdminImageGridAdapter.ImageHolder> {

    private List<String> images;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onClick(String url);
    }

    public AdminImageGridAdapter(List<String> images, OnImageClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        String url = images.get(position);
        Glide.with(holder.image.getContext())
                .load(url)
                .placeholder(R.drawable.ic_my_events)
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onClick(url));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.grid_image);
        }
    }
}
