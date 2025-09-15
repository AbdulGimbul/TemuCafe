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
import com.example.temucafe.models.Mall;

import java.util.List;

public class MallAdapter extends RecyclerView.Adapter<MallAdapter.ViewHolder> {

    private final Context context;
    private final List<Mall> mallList;
    private OnItemClickListener listener;

    public MallAdapter(Context context, List<Mall> mallList) {
        this.context = context;
        this.mallList = mallList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mall, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mall mall = mallList.get(position);
        holder.bind(mall, listener);
    }

    @Override
    public int getItemCount() {
        return mallList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Mall mall);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mallImage;
        TextView mallName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mallImage = itemView.findViewById(R.id.mall_image);
            mallName = itemView.findViewById(R.id.mall_name);
        }

        public void bind(final Mall mall, final OnItemClickListener listener) {
            mallName.setText(mall.getName());
            Glide.with(itemView.getContext()).load(mall.getImageUrl()).into(mallImage);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(mall);
                }
            });
        }
    }
}