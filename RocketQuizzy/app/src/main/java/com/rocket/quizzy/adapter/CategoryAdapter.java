package com.rocket.quizzy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.model.Category;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.QuizListActivity;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {

    Activity activity;

    public CategoryAdapter(@NonNull Context context,Activity activity, List<Category> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.category_item, parent, false);
        }

        TextView tvCategoryName = itemView.findViewById(R.id.categoryName);
        ImageView ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
        CardView cardView = itemView.findViewById(R.id.cardView);
        LottieAnimationView loading_bar = itemView.findViewById(R.id.loading_view);
        RelativeLayout content_view = itemView.findViewById(R.id.content_view);

        Category category = getItem(position);
        DayNight dayNight = new DayNight(getContext());
        dayNight.checkCardView(cardView);
        dayNight.checkContentView(content_view);
        dayNight.checkTextView(tvCategoryName,R.color.grey);

        tvCategoryName.setText(category.getName());

        itemView.setOnClickListener( v-> activity.startActivityForResult(new Intent(getContext().getApplicationContext(), QuizListActivity.class)
                .putExtra("categoryName",category.getName())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("categoryID",category.getId()), MainActivity.REQUEST_RETURN_HOME));

        if (!category.getThumbnail().equals("")){
            Glide.with(getContext()).load(category.getThumbnail()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loading_bar.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loading_bar.setVisibility(View.GONE);
                    return false;
                }
            }).placeholder(R.color.white).into(ivThumbnail);
        }
        return itemView;
    }
}
