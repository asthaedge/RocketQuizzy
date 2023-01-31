package com.rocket.quizzy.view;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.ActivityBadgeBinding;
import com.rocket.quizzy.service.DayNight;

public class BadgeActivity extends AppCompatActivity {

    ActivityBadgeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_badge);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Global.networkCheck(this);
        String level = getIntent().getStringExtra("qlevel");

        DayNight dayNight = new DayNight(this);

        dayNight.checkImageButton(binding.btnClose);
        dayNight.checkTextView(binding.tvHurray, R.color.themeColor);
        dayNight.checkContentView(binding.contentView);

        Uri uri = null;
        int iLevel = 0;


        if (level.equals(Global.LEVEL_1)) {
            Glide.with(BadgeActivity.this).load(R.drawable.level1_anim).into(binding.gif);
            iLevel = 1;
        } else if (level.equals(Global.LEVEL_2)) {
            Glide.with(BadgeActivity.this).load(R.drawable.level2_anim).into(binding.gif);
            iLevel = 2;
        } else if (level.equals(Global.LEVEL_3)) {
            Glide.with(BadgeActivity.this).load(R.drawable.level3_anim).into(binding.gif);
            iLevel = 3;
        } else if (level.equals(Global.LEVEL_4)) {
            Glide.with(BadgeActivity.this).load(R.drawable.level4_anim).into(binding.gif);
            iLevel = 4;
        } else if (level.equals(Global.LEVEL_5)) {
            Glide.with(BadgeActivity.this).load(R.drawable.level5_anim).into(binding.gif);
            iLevel = 5;
        }

        String strWinDesc = getString(R.string.badge_won_msg) + iLevel + getString(R.string.golden_badge);

        binding.tvWinDesc.setText(strWinDesc);

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}