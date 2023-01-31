package com.rocket.quizzy.startup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cuberto.liquid_swipe.LiquidPager;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.firebase.auth.FirebaseAuth;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.boardingScreens.OnBoardingFragment_1;
import com.rocket.quizzy.boardingScreens.OnBoardingFragment_2;
import com.rocket.quizzy.boardingScreens.OnBoardingFragment_3;
import com.rocket.quizzy.databinding.ActivitySplashScreenBinding;
import com.rocket.quizzy.model.Languages;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.auth.LoginActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;


public class SplashScreenActivity extends AppCompatActivity {

    ActivitySplashScreenBinding binding;

    private int SPLASH_SCREEN = 6000;
    private final static int NUM_PAGES = 3;
    private LiquidPager viewPager;
    private PagerAdapter pagerAdapter;
    SharedPreferences msharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        DayNight dayNight = new DayNight(this);
        dayNight.checkContentView(binding.contentView,R.color.spalsh_bg_color);

        viewPager = findViewById(R.id.pager);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        animate();


        String lang = Global.getLanguage(this);
        String langKey = Languages.getLanguageKey(lang);

        changeLanguage(langKey);

    }

    private void changeLanguage(String selectedLang) {
        Locale locale = new Locale(selectedLang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
    }

    private void animate() {
        binding.pager.setVisibility(View.GONE);
        ViewAnimator.animate(binding.ivLogo).fadeIn()
                .thenAnimate(binding.ivLogo).fadeOut().start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                msharedPreference = getSharedPreferences("SharedPref",MODE_PRIVATE);
                boolean isfirsttime = msharedPreference.getBoolean("firstTime", true);

                if (isfirsttime){
                    SharedPreferences.Editor editor = msharedPreference.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();

                    binding.pager.setVisibility(View.VISIBLE);
                    binding.ivLogo.setVisibility(View.GONE);

                    ViewAnimator.animate(binding.pager).dp().fadeIn().start();
                }else{
                        if (FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            Animatoo.animateZoom(SplashScreenActivity.this);
                            finish();
                        }else{
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            Animatoo.animateZoom(SplashScreenActivity.this);
                            finish();
                        }
                }


            }
        }, SPLASH_SCREEN);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(@NonNull @NotNull FragmentManager fm) {
            super(fm);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    OnBoardingFragment_1 tab1 = new OnBoardingFragment_1();
                    return tab1;
                case 1:
                    OnBoardingFragment_2 tab2 = new OnBoardingFragment_2();
                    return tab2;
                case 2:
                    OnBoardingFragment_3 tab3 = new OnBoardingFragment_3();
                    return tab3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}