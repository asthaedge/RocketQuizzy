package com.rocket.quizzy.service;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;


public class DayNight {

    Context context;
    boolean isNight;

    public DayNight(Context context) {
        this.context = context;
        this.isNight = Global.isDarkMode(context);
    }

    public void checkContentView(@NonNull View v){
            v.setBackgroundResource((isNight) ? R.color.darkColor : R.color.light_sky);
    }

    public void checkDialogContentView(@NonNull View v){
        v.setBackgroundResource((isNight) ? R.drawable.dark_corner : R.drawable.white_corner);
    }

    public void checkContentView(@NonNull View v, int lightColor){
        v.setBackgroundResource((isNight) ? R.color.darkColor : lightColor);
    }

    public void checkToolbar(@NonNull Toolbar toolbar){
        toolbar.setBackgroundResource((isNight) ? R.drawable.theme_bg_down_corner_dark : R.drawable.theme_bg_down_corner_light);
    }

    public void checkImageView(@NonNull ImageView iv){
        ImageViewCompat.setImageTintList(iv, ColorStateList.valueOf((isNight) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.themeColor)));
    }

    public void checkImageButton(@NonNull ImageButton iv){
        iv.setColorFilter((isNight) ? R.color.white : R.color.themeColor);
    }

    public void checkTextView(@NonNull TextView tv,int lightColor){
        tv.setTextColor((isNight) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(lightColor));
        setTextViewDrawableColor(tv);

    }

    public void checkTextView(@NonNull TextView tv,int lightColor, int darkColor){
        tv.setTextColor((isNight) ? context.getResources().getColor(darkColor) : context.getResources().getColor(lightColor));
        setTextViewDrawableColor(tv);

    }

    public void checkEditText(@NonNull EditText edtv){
        edtv.setTextColor((isNight) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black));
        edtv.setHintTextColor((isNight) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.grey));
    }

    public void checkButton(@NonNull Button btn){
        btn.setTextColor((isNight) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.themeColor));
    }

    public void checkCardView(@NonNull CardView cardView){
        cardView.setCardBackgroundColor((isNight) ? context.getResources().getColor(R.color.darkLight) : context.getResources().getColor(R.color.white));
    }

    public void checkLogoutButton(@NonNull CardView cardView,TextView textView, ImageView imageView){
        cardView.setCardBackgroundColor((isNight) ? context.getResources().getColor(R.color.red) : context.getResources().getColor(R.color.white));
        textView.setTextColor((isNight) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.red));
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf((isNight) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.red)));
    }

    public void checkBottomNavigationView(@NonNull BottomNavigationView bottomNavigationView){
        bottomNavigationView.setBackgroundResource((isNight) ? R.color.darkLight : R.color.white);
        bottomNavigationView.setItemTextColor(ColorStateList.valueOf(context.getResources().getColor((isNight) ? R.color.white : R.color.themeColor)));
        bottomNavigationView.setItemIconTintList((isNight) ? ColorStateList.valueOf(context.getResources().getColor(R.color.white)) : null);
    }

    private void setTextViewDrawableColor(TextView textView) {
        for (Drawable drawable : textView.getCompoundDrawablesRelative()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), (isNight) ? R.color.white : R.color.themeColor), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void checkCardView(CardView cardView, int lightColor, int darkColor) {
        cardView.setCardBackgroundColor((isNight) ? context.getResources().getColor(darkColor) : context.getResources().getColor(lightColor));
    }

}
