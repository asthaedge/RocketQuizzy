package com.rocket.quizzy.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.model.Quiz;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.CustomQuizInfoActivity;
import com.rocket.quizzy.view.QuizActivity;
import com.rocket.quizzy.view.QuizInfoActivity;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.Holder> {

    List<Quiz> list;
    Context context;
    private int CASE_NORMAL = 1;
    private int CASE_TOP = 2;
    boolean isTopCall;
    Activity activity;

    public QuizAdapter(List<Quiz> list, Context context, boolean isTopCall,Activity activity) {
        this.list = list;
        this.context = context;
        this.isTopCall = isTopCall;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CASE_NORMAL){
            return new Holder(LayoutInflater.from(context).inflate(R.layout.quiz_item,parent,false));
        }else {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.top_item,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        Quiz quiz = list.get(position);

        DayNight dayNight = new DayNight(context);
        dayNight.checkCardView(holder.cardView);
        dayNight.checkContentView(holder.itemView);
        if (quiz.isTop()){
            dayNight.checkTextView(holder.tvTitle,R.color.grey);
        }else {
            dayNight.checkTextView(holder.tvDesc,R.color.grey);
        }

        Glide.with(context).load(quiz.getQuizThumbnail()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.loding_bar.setVisibility(View.VISIBLE);
                Toast.makeText(context, context.getText(R.string.network_error), Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.loding_bar.setVisibility(View.GONE);
                return false;
            }
        }).placeholder(R.color.white).into(holder.ivThumbnail);

        holder.tvTitle.setText(quiz.getQuizTitle());

        if (!isTopCall){
            holder.tvDesc.setText(quiz.getQuizDescription());
        }

        holder.itemView.setOnClickListener(v -> {

            Dexter.withActivity(activity).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    Intent i = new Intent(context.getApplicationContext(), CustomQuizInfoActivity.class);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<ImageView,String>(holder.ivThumbnail,"thumbnail");

                    Bitmap bmp = ((BitmapDrawable)holder.ivThumbnail.getDrawable()).getBitmap();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    i.putExtra("qtitle",quiz.getQuizTitle());
                    i.putExtra("qDesc",quiz.getQuizDescription());
                    i.putExtra("thumbnail",byteArray);

                    if (isTopCall){
                        i.putExtra("isCameFromHomeFragment",true);
                    }

                    i.putExtra("qID",quiz.getId());

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,pairs);

                    activity.startActivityForResult(i, MainActivity.REQUEST_RETURN_HOME,options.toBundle());

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    Toast.makeText(context, context.getString(R.string.permit_notice), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();

        });

        holder.playBtn.setOnClickListener(v -> {

            Intent i = new Intent(context.getApplicationContext(),QuizActivity.class);
            i.putExtra("qID", quiz.getId());
            i.putExtra("isCameFromHomeFragment", isTopCall);
            i.putExtra("quizLevel", Global.PUBLIC_LEVEL_KEY);

            activity.startActivityForResult(i,MainActivity.REQUEST_RETURN_HOME);
        });


    }

    @Override
    public int getItemViewType(int position) {
        if (isTopCall){
            return CASE_TOP;
        }else {
            return CASE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Quiz> filteredList, RecyclerView quizRecycler, TextView blankRecyclerTv) {

        list = filteredList;

        notifyDataSetChanged();

        if (list.size() > 0){
            quizRecycler.setVisibility(View.VISIBLE);
            blankRecyclerTv.setVisibility(View.GONE);
        }else {
            quizRecycler.setVisibility(View.GONE);
            blankRecyclerTv.setVisibility(View.VISIBLE);
        }

    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView ivThumbnail;
        TextView tvTitle,tvDesc;
        LottieAnimationView playBtn,loding_bar;
        CardView cardView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            playBtn = itemView.findViewById(R.id.play_btn);
            loding_bar = itemView.findViewById(R.id.loading_view);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
