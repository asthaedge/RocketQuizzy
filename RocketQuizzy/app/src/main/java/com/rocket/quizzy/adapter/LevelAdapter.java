package com.rocket.quizzy.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.model.Level;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.QuizInfoActivity;
import com.rocket.quizzy.view.auth.LoginActivity;
import com.rocket.quizzy.view.custom.Loading_Dialog;
import com.rocket.quizzy.view.fragments.MagicPlayFragment;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.Holder> {
    List<Level> list;
    Context context;
    Activity activity;
    MagicPlayFragment magicPlayFragment;
    Loading_Dialog loading_dialog;

    public LevelAdapter(List<Level> list, Context context,Activity activity, MagicPlayFragment magicPlayFragment, Loading_Dialog loading_dialog) {
        this.list = list;
        this.context = context;
        this.magicPlayFragment = magicPlayFragment;
        this.activity = activity;
        this.loading_dialog = loading_dialog;
    }


    @NonNull
    @Override
    public LevelAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_level, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LevelAdapter.Holder holder, int position) {

        Level badge = list.get(position);

        boolean isUserLoggedIn = false;

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            isUserLoggedIn = false;
        } else {
            isUserLoggedIn = true;
        }

        DayNight dayNight = new DayNight(context);

        dayNight.checkContentView(holder.itemView);

        loading_dialog.dismissDialog();

        String level = "";
        int ivlevel = 0;
        int ivlevelBG = 0;

        if (badge.getLevel() == 1) {
            level = context.getString(R.string.level_1);
            ivlevel = R.drawable.level1;
            ivlevelBG = R.drawable.level_design_1;
                    } else if (badge.getLevel() == 2) {
            level = context.getString(R.string.level_2);
            ivlevel = R.drawable.level2;
            ivlevelBG = R.drawable.level_design_2;
        } else if (badge.getLevel() == 3) {
            level = context.getString(R.string.level_3);
            ivlevel = R.drawable.level3;
            ivlevelBG = R.drawable.level_design_3;
        } else if (badge.getLevel() == 4) {
            level = context.getString(R.string.level_4);
            ivlevel = R.drawable.level4;
            ivlevelBG = R.drawable.level_design_4;
        } else if (badge.getLevel() == 5) {
            level = context.getString(R.string.level_5);
            ivlevel = R.drawable.level5;
            ivlevelBG = R.drawable.level_design_5;
        }

        if (magicPlayFragment.lastachievedlevel!=-1){

            if (badge.getLevel()==magicPlayFragment.lastachievedlevel+1){

                holder.imageButton.setVisibility(View.GONE);
                holder.btn_play.setVisibility(View.VISIBLE);

            }else {

                if (badge.isAchieved()){
                    holder.imageButton.setImageResource(R.drawable.ic_baseline_check_24);
                    holder.btn_play.setVisibility(View.GONE);
                    holder.imageButton.setVisibility(View.VISIBLE);
                }else {
                    holder.imageButton.setImageResource(R.drawable.ic_baseline_lock_24);
                    holder.btn_play.setVisibility(View.GONE);
                    holder.imageButton.setVisibility(View.VISIBLE);
                }

            }

        }


        holder.tv_level.setText(level);
        if (ivlevel!=0){
            Glide.with(context).load(ivlevel).into(holder.iv_level);
        }
        if (ivlevelBG!=0){
            holder.linearLayout.setBackground(context.getDrawable(ivlevelBG));
        }
        holder.tv_title.setText(badge.getLevelTitle());


        boolean finalIsUserLoggedIn = isUserLoggedIn;
        holder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!finalIsUserLoggedIn) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                    ((Activity) context).finish();
                } else {
                    holder.btn_play.playAnimation();
                    Dexter.withActivity(activity).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            if (badge.getLevel() == magicPlayFragment.lastachievedlevel + 1 || badge.isAchieved()) {

                                Intent i = new Intent(context.getApplicationContext(), QuizInfoActivity.class);
                                i.putExtra("qlevel", badge.getLevel());
                                i.putExtra("qleveltitle", badge.getLevelTitle());

                                activity.startActivityForResult(i, MainActivity.REQUEST_RETURN_MAGIC_PLAY);
                                Animatoo.animateZoom(activity);

                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            Toast.makeText(context.getApplicationContext(), context.getText(R.string.permit_notice), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
                }
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (badge.isAchieved()){
                    Dexter.withActivity(activity).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            if (badge.getLevel()==magicPlayFragment.lastachievedlevel+1 || badge.isAchieved()){

                                Intent i = new Intent(context.getApplicationContext(), QuizInfoActivity.class);
                                i.putExtra("qlevel",badge.getLevel());
                                i.putExtra("qleveltitle",badge.getLevelTitle());

                                activity.startActivityForResult(i,1234);
                                Animatoo.animateZoom(activity);

                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            Toast.makeText(context.getApplicationContext(), context.getText(R.string.permit_notice), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        LottieAnimationView btn_play;
        TextView tv_title, tv_level;
        LinearLayout linearLayout;
        ImageView iv_level;
        ImageButton imageButton;

        public Holder(@NonNull View itemView) {
            super(itemView);
            btn_play = itemView.findViewById(R.id.play_btn);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_level = itemView.findViewById(R.id.tv_level);
            linearLayout = itemView.findViewById(R.id.level_LL);
            iv_level = itemView.findViewById(R.id.iv_level);
            imageButton = itemView.findViewById(R.id.image_btn);
        }
    }
}
