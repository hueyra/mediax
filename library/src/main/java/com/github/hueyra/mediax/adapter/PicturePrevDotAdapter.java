package com.github.hueyra.mediax.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.hueyra.mediax.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujun
 * Date : 2022-03-25
 * Desc : _
 */
public class PicturePrevDotAdapter extends RecyclerView.Adapter<PicturePrevDotAdapter.ViewHolder> {

    private List<Boolean> data;

    public PicturePrevDotAdapter(List<Boolean> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PicturePrevDotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media_prev_dot, parent, false);
        return new PicturePrevDotAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ivImage.setImageResource(data.get(position) ? R.drawable.bg_img_prev_indicator_select : R.drawable.bg_img_prev_indicator_unselect);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void bindData(List<Boolean> data) {
        this.data = data == null ? new ArrayList<>() : data;
        this.notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_dot);
        }
    }


}
