package com.rocket.quizzy.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rocket.quizzy.R;
import com.rocket.quizzy.service.DayNight;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.Holder> {

    List<String> list;
    private ViewPager2 viewPager2;
    Context context;

    public SliderAdapter(List<String> list, Context context,ViewPager2 viewPager2) {
        this.list = list;
        this.context = context;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_slider,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.Holder holder, int position) {
        String currentImage = list.get(position);

        DayNight dayNight = new DayNight(context);
        dayNight.checkContentView(holder.itemView);

        Glide.with(context).load(currentImage).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.loading_bar.setVisibility(View.VISIBLE);
                Toast.makeText(context, context.getText(R.string.network_error), Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.loading_bar.setVisibility(View.GONE);
                return false;
            }
        }).placeholder(R.color.white).into(holder.imageView);

        if (position == list.size() - 2){
            viewPager2.post(runnable);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            list.addAll(list);
            notifyDataSetChanged();
        }
    };

    public class Holder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageView loading_bar;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivThumbnail);
            loading_bar = itemView.findViewById(R.id.loading_view);

        }
    }

}
