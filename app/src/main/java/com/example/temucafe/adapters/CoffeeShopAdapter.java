package com.example.temucafe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.temucafe.R;
import com.example.temucafe.models.CoffeeShop;

import java.util.List;

public class CoffeeShopAdapter extends RecyclerView.Adapter<CoffeeShopAdapter.ViewHolder> {

    private final Context context;
    private final List<CoffeeShop> coffeeShopList;
    private OnItemClickListener listener;

    public CoffeeShopAdapter(Context context, List<CoffeeShop> coffeeShopList) {
        this.context = context;
        this.coffeeShopList = coffeeShopList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coffeeshop_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoffeeShop coffeeShop = coffeeShopList.get(position);
        holder.bind(coffeeShop, listener);
    }

    @Override
    public int getItemCount() {
        return coffeeShopList.size();
    }

    // Interface to handle click events
    public interface OnItemClickListener {
        void onItemClick(CoffeeShop coffeeShop);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coffeeShopImage;
        TextView coffeeShopName;
        TextView coffeeShopLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coffeeShopImage = itemView.findViewById(R.id.coffeeshop_image);
            coffeeShopName = itemView.findViewById(R.id.coffeeshop_name);
            coffeeShopLocation = itemView.findViewById(R.id.coffeeshop_location);
        }

        public void bind(final CoffeeShop coffeeShop, final OnItemClickListener listener) {
            coffeeShopName.setText(coffeeShop.getName());
//            coffeeShopLocation.setText(coffeeShop.getLocation());

            // Use Glide to load the image from a URL
            Glide.with(itemView.getContext())
                    .load(coffeeShop.getLogoIconUrl())
                    .placeholder(R.drawable.coffeshop_home_glass_coffe) // Default image
                    .error(R.drawable.coffeshop_home_glass_coffe) // Image on error
                    .into(coffeeShopImage);

            // Set the click listener on the entire item view
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(coffeeShop);
                }
            });
        }
    }
}