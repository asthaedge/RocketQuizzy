package com.rocket.quizzy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rocket.quizzy.R;
import com.rocket.quizzy.model.Badge;

import java.util.List;

/**
 * This is a adapter for managing badges...
 */

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.Holder> {
    List<Badge> list;
    Context context;

    public BadgeAdapter(List<Badge> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BadgeAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeAdapter.Holder holder, int position) {

        Badge badge = list.get(position);

        if (badge.getLevel()==1){
            if (badge.isAchieved()){
                holder.ivBadge.setImageResource(R.drawable.level_1);
                holder.tvBadge.setText(R.string.level_1);
            }else {
                holder.ivBadge.setImageResource(R.drawable.level_1_e);
            }
        }else if (badge.getLevel()==2){
            if (badge.isAchieved()){
                holder.ivBadge.setImageResource(R.drawable.level_2);
                holder.tvBadge.setText(R.string.level_2);
            }else {
                holder.ivBadge.setImageResource(R.drawable.level_2_e);
            }
        }else if (badge.getLevel()==3){
            if (badge.isAchieved()){
                holder.ivBadge.setImageResource(R.drawable.level_3);
                holder.tvBadge.setText(R.string.level_3);
            }else {
                holder.ivBadge.setImageResource(R.drawable.level_3_e);
            }
        }else if (badge.getLevel()==4){
            if (badge.isAchieved()){
                holder.ivBadge.setImageResource(R.drawable.level_4);
                holder.tvBadge.setText(R.string.level_4);
            }else {
                holder.ivBadge.setImageResource(R.drawable.level_4_e);
            }
        }else if (badge.getLevel()==5){
            if (badge.isAchieved()){
                holder.ivBadge.setImageResource(R.drawable.level_5);
                holder.tvBadge.setText(R.string.level_5);
            }else {
                holder.ivBadge.setImageResource(R.drawable.level_5_e);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        ImageView ivBadge;
        TextView tvBadge;
        public Holder(@NonNull View itemView) {
            super(itemView);
            ivBadge = itemView.findViewById(R.id.iv_badge);
            tvBadge = itemView.findViewById(R.id.tv_level);
        }
    }
}
