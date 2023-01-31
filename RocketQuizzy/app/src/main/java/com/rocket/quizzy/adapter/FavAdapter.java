package com.rocket.quizzy.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.rocket.quizzy.view.custom.Loading_Dialog;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    List<Quiz> list;
    Context context;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    Activity activity;
    Loading_Dialog loading_dialog;

    public FavAdapter(List<Quiz> list, Context context,Activity activity) {
        this.list = list;
        this.context = context;
        loading_dialog = new Loading_Dialog((Activity) context);
        this.activity = activity;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(Global.USERS).child(firebaseUser.getUid()).child(Global.FAVOURITE);
    }

    @NonNull
    @Override
    public FavAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fav, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavAdapter.ViewHolder holder, int position) {
        Quiz quiz = list.get(position);

        DayNight dayNight = new DayNight(context);
        dayNight.checkCardView(holder.cardView);
        dayNight.checkTextView(holder.tv_desc, R.color.grey);
        dayNight.checkContentView(holder.itemView);

        Glide.with(context).load(quiz.getQuizThumbnail()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.loding_bar.setVisibility(View.VISIBLE);
                Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.loding_bar.setVisibility(View.GONE);
                return false;
            }
        }).placeholder(R.color.white).into(holder.ivThumbnail);

        holder.tv_title.setText(quiz.getQuizTitle());

        holder.tv_desc.setText(quiz.getQuizDescription());

        holder.delete_btn.setOnClickListener(v -> {

            loading_dialog.startLoadingDialog();

            deleteItem(holder.itemView, position);

        });

        holder.play_btn.setOnClickListener(v -> {
            activity.startActivityForResult(new Intent(context.getApplicationContext(), QuizActivity.class)
                    .putExtra("qID", quiz.getId())
                    .putExtra("fromFavFragment", true)
                    .putExtra("quizLevel", Global.PUBLIC_LEVEL_KEY), MainActivity.REQUEST_RETURN_FAV);
        });

        holder.itemView.setOnClickListener(v -> {

            Dexter.withActivity(activity).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    Intent i = new Intent(context.getApplicationContext(), CustomQuizInfoActivity.class);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<>(holder.ivThumbnail, "thumbnail");

                    Bitmap bmp = ((BitmapDrawable) holder.ivThumbnail.getDrawable()).getBitmap();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    i.putExtra("qtitle", quiz.getQuizTitle());
                    i.putExtra("qDesc", quiz.getQuizDescription());
                    i.putExtra("thumbnail", byteArray);
                    i.putExtra("fromFavFragment", true);

                    i.putExtra("qID", quiz.getId());

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                    activity.startActivityForResult(i,MainActivity.REQUEST_RETURN_FAV,options.toBundle());

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    Toast.makeText(context, context.getText(R.string.permit_notice), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();

        });


    }

    private void deleteItem(View v, int position) {

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);

        animation.setDuration(500);

        v.startAnimation(animation);

        new Handler().postDelayed(() -> {
            if (list.size() == 0) {
                return;
            }
            removeFromOnlineDB(list.get(position).getId(), new RemovalStatusListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess() {
                    loading_dialog.dismissDialog();
                    list.remove(position);
                    notifyDataSetChanged();
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure() {
                    notifyDataSetChanged();
                    loading_dialog.dismissDialog();
                    Toast.makeText(context, context.getText(R.string.sth_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });
        }, animation.getDuration());


    }

    private void removeFromOnlineDB(String id, RemovalStatusListener listener) {

        reference.child(id).removeValue().addOnSuccessListener(unused -> listener.onSuccess()).addOnFailureListener(e -> listener.onFailure());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        Button delete_btn, play_btn;
        ImageView ivThumbnail;
        TextView tv_title, tv_desc;
        LottieAnimationView loding_bar;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            delete_btn = itemView.findViewById(R.id.delete_btn);
            play_btn = itemView.findViewById(R.id.play_btn);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_desc = itemView.findViewById(R.id.tv_desc);
            loding_bar = itemView.findViewById(R.id.loading_view);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}

interface RemovalStatusListener {
    void onSuccess();

    void onFailure();
}

