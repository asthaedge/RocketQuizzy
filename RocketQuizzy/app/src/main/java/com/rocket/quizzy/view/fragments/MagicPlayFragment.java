package com.rocket.quizzy.view.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.adapter.LevelAdapter;
import com.rocket.quizzy.databinding.FragmentMagicPlayBinding;
import com.rocket.quizzy.model.Level;
import com.rocket.quizzy.model.User;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.AddQuizActivity;
import com.rocket.quizzy.view.BadgeActivity;
import com.rocket.quizzy.view.auth.LoginActivity;
import com.rocket.quizzy.view.custom.Alert_Dialog;
import com.rocket.quizzy.view.custom.Loading_Dialog;
import com.rocket.quizzy.view.custom.listeners.Alert_Listener;

import java.util.ArrayList;
import java.util.List;


public class MagicPlayFragment extends Fragment {


    public MagicPlayFragment() {
        // Required empty public constructor
    }

    FragmentMagicPlayBinding binding;
    public int lastachievedlevel = -1;
    public int REQUEST_BADGE_ACHIEVEMENT = 1234;
    boolean level1Achieved = false;
    boolean level2Achieved = false;
    boolean level3Achieved = false;
    boolean level4Achieved = false;
    boolean level5Achieved = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_magic_play, container, false);

        DayNight dayNight = new DayNight(getActivity());
        dayNight.checkContentView(binding.contentView);

        if (Global.isPro(getActivity())) {
            binding.proTag.setVisibility(View.VISIBLE);
            //         binding.addQuizBtn.setVisibility(View.VISIBLE);
        } else {
            binding.proTag.setVisibility(View.GONE);
            //     binding.addQuizBtn.setVisibility(View.GONE);
        }

        getLevels();


//        binding.addQuizBtn.setOnClickListener(v -> {
//            startActivity(new Intent(getActivity().getApplicationContext(), AddQuizActivity.class));
//        });


        return binding.getRoot();
    }

    private void getLevels() {
        Loading_Dialog loading_dialog = new Loading_Dialog(getActivity());
        loading_dialog.startLoadingDialog();

        List<Level> list = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                User user = snapshot.getValue(User.class);

                //           String fullName = user.getName();
                //           String[] arrayFullName = fullName.split(" ");
                //           String firstName = arrayFullName[0];
                //           binding.tvName.setText("Hi, "+firstName);


                lastachievedlevel = 0;

                if (firebaseUser != null) {

                    DataSnapshot ds = snapshot.child(Global.USERS).child(firebaseUser.getUid());

                    if (ds.hasChild(Global.LEVEL_1)) {
                        level1Achieved = true;
                        lastachievedlevel = 1;
                    }

                    if (ds.hasChild(Global.LEVEL_2)) {
                        level2Achieved = true;
                        lastachievedlevel = 2;
                    }

                    if (ds.hasChild(Global.LEVEL_3)) {
                        level3Achieved = true;
                        lastachievedlevel = 3;
                    }

                    if (ds.hasChild(Global.LEVEL_4)) {
                        level4Achieved = true;
                        lastachievedlevel = 4;
                    }

                    if (ds.hasChild(Global.LEVEL_5)) {
                        level5Achieved = true;
                        lastachievedlevel = 5;
                    }
                }

                if (level5Achieved) {
                    binding.proTag.setVisibility(View.VISIBLE);
                 //   binding.addQuizBtn.setVisibility(View.VISIBLE);
                } else {
                    binding.proTag.setVisibility(View.GONE);
           //         binding.addQuizBtn.setVisibility(View.GONE);
                }

                list.add(new Level(1, level1Achieved, getString(R.string.beginner)));
                list.add(new Level(2, level2Achieved, getString(R.string.continuing)));
                list.add(new Level(3, level3Achieved, getString(R.string.travel_newbie)));
                list.add(new Level(4, level4Achieved, getString(R.string.experienced)));
                list.add(new Level(5, level5Achieved, getString(R.string.zen_master)));

                loading_dialog.dismissDialog();

                LevelAdapter levelAdapter = new LevelAdapter(list, getActivity(), getActivity(), MagicPlayFragment.this,
                        loading_dialog);
                binding.recyclerView.setAdapter(levelAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BADGE_ACHIEVEMENT
                && resultCode == getActivity().RESULT_OK) {
            boolean isBadgeAchieved = data.getBooleanExtra("isBadgeAchieved", false);
            boolean isFirstTime = data.getBooleanExtra("isFirstTime", false);
            String qlevel = data.getStringExtra("level");

            if (isBadgeAchieved) {
                if (isFirstTime) {
                    startActivity(new Intent(getActivity().getApplicationContext(), BadgeActivity.class)
                            .putExtra("qlevel", qlevel));
                }
            }

        }
    }
}