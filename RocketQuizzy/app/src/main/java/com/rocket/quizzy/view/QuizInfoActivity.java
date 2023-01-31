package com.rocket.quizzy.view;

import android.content.Intent;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.ActivityQuizInfoBinding;
import com.rocket.quizzy.service.DayNight;

public class QuizInfoActivity extends AppCompatActivity {

    private ActivityQuizInfoBinding binding;
    String strLevel;
    private int ivlevel = 0;

    public int REQUEST_BADGE_ACHIEVEMENT = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_info);
        Global.networkCheck(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DayNight dayNight = new DayNight(this);

        dayNight.checkContentView(binding.contentView);
        dayNight.checkTextView(binding.tv2, R.color.themeColor);
        dayNight.checkButton(binding.startBtn);
        dayNight.checkCardView(binding.startBtnLayout);

        int level = getIntent().getIntExtra("qlevel", 0);
        String levelTitle = getIntent().getStringExtra("qleveltitle");

        binding.tvLevel.setText( getString(R.string.level) + level);
        binding.tvLevelTitle.setText(levelTitle);


        if (level == 1) {
            strLevel = Global.LEVEL_1;
            ivlevel = R.drawable.level1;
        } else if (level == 2) {
            strLevel = Global.LEVEL_2;
            ivlevel = R.drawable.level2;
        } else if (level == 3) {
            strLevel = Global.LEVEL_3;
            ivlevel = R.drawable.level3;
        } else if (level == 4) {
            strLevel = Global.LEVEL_4;
            ivlevel = R.drawable.level4;
        } else if (level == 5) {
            strLevel = Global.LEVEL_5;
            ivlevel = R.drawable.level5;
        }

        Glide.with(QuizInfoActivity.this).load(ivlevel).into(binding.ivLevel);

        binding.ivLevel.setScaleType(ImageView.ScaleType.FIT_CENTER);

        binding.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), QuizActivity.class)
                        .putExtra("quizLevel", strLevel),REQUEST_BADGE_ACHIEVEMENT);
                Animatoo.animateZoom(QuizInfoActivity.this);
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra(Global.REQUEST_QUIT_KEY,Global.MAGIC_PLAY_REQUEST);
                startActivity(i);
                Animatoo.animateZoom(QuizInfoActivity.this);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra(Global.REQUEST_QUIT_KEY,Global.MAGIC_PLAY_REQUEST);
        startActivity(i);
        Animatoo.animateZoom(QuizInfoActivity.this);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BADGE_ACHIEVEMENT
                && resultCode == RESULT_OK){
            boolean isBadgeAchieved = data.getBooleanExtra("isBadgeAchieved",false);
            boolean isFirstTime = data.getBooleanExtra("isFirstTime",false);
            String qlevel = data.getStringExtra("level");

            Intent intent = new Intent();
            intent.putExtra("isBadgeAchieved",isBadgeAchieved);
            intent.putExtra("isFirstTime",isFirstTime);
            intent.putExtra("level",qlevel);
            setResult(RESULT_OK,intent);
            finish();
        }else {
            finish();
        }
    }
}