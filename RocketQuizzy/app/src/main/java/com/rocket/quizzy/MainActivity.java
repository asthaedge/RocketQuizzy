package com.rocket.quizzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.adapter.SliderAdapter;
import com.rocket.quizzy.databinding.ActivityMainBinding;
import com.rocket.quizzy.service.BaseFragment;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.BadgeActivity;
import com.rocket.quizzy.view.fragments.FavFragment;
import com.rocket.quizzy.view.fragments.HomeFragment;
import com.rocket.quizzy.view.fragments.MagicPlayFragment;
import com.rocket.quizzy.view.fragments.ProfileFragment;
import com.rocket.quizzy.view.fragments.SettingsFragment;
import com.rocket.quizzy.view.fragments.SignInFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ViewPager2 sliderViewPager;
    private Handler sliderHandler = new Handler();
    public static final int REQUEST_RETURN_HOME = 1;
    public static final int REQUEST_RETURN_MAGIC_PLAY = 2;
    public static final int REQUEST_RETURN_FAV = 3;
    public static final int REQUEST_RETURN_SETTING = 4;
    public static final int REQUEST_RETURN_PROFILE = 5;
    List<String> list;
    public int REQUEST_BADGE_ACHIEVEMENT = 1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Global.networkCheck(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DayNight dayNight = new DayNight(this);
        dayNight.checkBottomNavigationView(binding.bottomBar);

        File file = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));

        if (!file.exists()) {
            file.mkdir();
        }


        binding.bottomBar.setOnItemSelectedListener(listener);

        sliderViewPager = findViewById(R.id.slider_view_pager);

        setSlider();

    }

    public BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;
            boolean isUserLoggedIn = false;

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                isUserLoggedIn = false;
            } else {
                isUserLoggedIn = true;
            }

            switch (item.getItemId()) {
//                case R.id.home:
//                    fragment = new HomeFragment();
//                    break;
                case R.id.magic_play:
             //       if (isUserLoggedIn) {
                        fragment = new MagicPlayFragment();
          //          } else {
           //             fragment = new SignInFragment();
            //        }
            //        break;
           //     case R.id.favourite:
           //         if (isUserLoggedIn) {
           //             fragment = new FavFragment();
           //         } else {
           //             fragment = new SignInFragment();
           //         }
                    break;
                case R.id.settings:
                        fragment = new SettingsFragment();
                    break;
                case R.id.profile:
                    if (isUserLoggedIn) {
                        fragment = new ProfileFragment();
                    } else {
                        fragment = new SignInFragment();
                    }
                    break;

            }

            getSupportFragmentManager().beginTransaction().replace(binding.frameLayout.getId(), fragment).commit();
            return true;
        }
    };


    private void setSlider() {
        if (sliderViewPager != null) {
            sliderViewPager.setVisibility(View.VISIBLE);
            Query query = FirebaseDatabase.getInstance().getReference(Global.MISC).child(Global.BANNER);

            query.keepSynced(true);

            list = new ArrayList<>();
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String url = ds.child(Global.BANNER_URL).getValue().toString();
                        list.add(url);
                    }

                    if (list.size() > 0) {
                        sliderViewPager.setVisibility(View.VISIBLE);
                    } else {
                        sliderViewPager.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            });

      //      if (list.size() > 0) {
      //          sliderViewPager.setAdapter(new SliderAdapter(list, getApplicationContext(), sliderViewPager));
//
      //          sliderViewPager.setClipToPadding(false);
      //          sliderViewPager.setClipChildren(false);
      //          sliderViewPager.setOffscreenPageLimit(3);
      //          sliderViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
//
      //          CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
      //          compositePageTransformer.addTransformer(new MarginPageTransformer(40));
      //          compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
      //              @Override
      //              public void transformPage(@NonNull View page, float position) {
      //                  float r = 1 - Math.abs(position);
      //                  page.setScaleY(0.85f + r * 0.15f);
      //              }
      //          });
//
      //          sliderViewPager.setPageTransformer(compositePageTransformer);
//
      //          sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      //              @Override
      //              public void onPageSelected(int position) {
      //                  super.onPageSelected(position);
      //                  sliderHandler.removeCallbacks(sliderRunnable);
      //                  sliderHandler.postDelayed(sliderRunnable, 3000); // slide duration 2 seconds
      //              }
      //          });
      //      }
        }
    }

    @Override
    public void onBackPressed() {
        List fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        for (Object f : fragmentList) {
            if (f instanceof BaseFragment) {
                handled = ((BaseFragment) f).onBackPressed();

                if (handled) {
                    break;
                }
            }
        }

        if (!handled) {
            super.onBackPressed();
        }
    }

  //  private Runnable sliderRunnable = new Runnable() {
  //      @Override
  //      public void run() {
  //          if (sliderViewPager != null) {
  //              if (list.size() > 0) {
  //                  sliderViewPager.setCurrentItem(sliderViewPager.getCurrentItem() + 1);
  //              }
  //          }
  //      }
  //  };

    @Override
    protected void onStart() {
        super.onStart();
        String return_request ="";
        if (getIntent().hasExtra(Global.REQUEST_QUIT_KEY)){
            return_request = getIntent().getStringExtra(Global.REQUEST_QUIT_KEY);
        }

      //  if (return_request.equals(Global.HOME_REQUEST)){
      //      binding.bottomBar.setSelectedItemId(R.id.home);
      //  }
      /* else */ if (return_request.equals(Global.MAGIC_PLAY_REQUEST)){
            binding.bottomBar.setSelectedItemId(R.id.magic_play);
     //  }
      //  else if (return_request.equals(Global.FAV_REQUEST)){
    //       binding.bottomBar.setSelectedItemId(R.id.favourite);
       }
        else if (return_request.equals(Global.SETTINGS_REQUEST)){
            binding.bottomBar.setSelectedItemId(R.id.settings);
        }else if (return_request.equals(Global.PROFILE_REQUEST)){
            binding.bottomBar.setSelectedItemId(R.id.profile);
        }else {
     //       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MagicPlayFragment()).commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
   //     sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
  //      sliderHandler.postDelayed(sliderRunnable, 3000);
    }

}