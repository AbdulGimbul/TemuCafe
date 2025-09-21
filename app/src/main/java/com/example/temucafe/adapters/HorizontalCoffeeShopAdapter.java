// app/src/main/java/com/example/temucafe/adapters/HorizontalCoffeeShopAdapter.java
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

public class HorizontalCoffeeShopAdapter extends RecyclerView.Adapter<HorizontalCoffeeShopAdapter.ViewHolder> {

    private final Context context;
    private final List<CoffeeShop> coffeeShopList;
    private OnItemClickListener listener;

    public HorizontalCoffeeShopAdapter(Context context, List<CoffeeShop> coffeeShopList) {
        this.context = context;
        this.coffeeShopList = coffeeShopList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the horizontal item layout
        View view = LayoutInflater.from(context).inflate(R.layout.industrial_coffee_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(coffeeShopList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return coffeeShopList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CoffeeShop coffeeShop);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coffeeShopImage;
        TextView coffeeShopName;
        TextView coffeeShopLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coffeeShopImage = itemView.findViewById(R.id.industrial_coffee_image);
            coffeeShopName = itemView.findViewById(R.id.industrial_coffee_name);
            coffeeShopLocation = itemView.findViewById(R.id.industrial_coffee_location);
        }

        public void bind(final CoffeeShop coffeeShop, final OnItemClickListener listener) {
            coffeeShopName.setText(coffeeShop.getName());
//            coffeeShopLocation.setText(coffeeShop.getLocation());
            Glide.with(itemView.getContext()).load(coffeeShop.getLogoIconUrl()).into(coffeeShopImage);
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(coffeeShop);
            });
        }
    }
}