package com.rocket.quizzy.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.ActivityScoreBinding;
import com.rocket.quizzy.service.DayNight;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    ActivityScoreBinding binding;
    int score;
    String level;
    int skippedQuestions;
    int totalQuestions;
    int unviwedQuestions;
    ArrayList<String> userSelectedOptions;
    public int REQUEST_BADGE_ACHIEVEMENT = 1234;
    boolean isFirstTime = false;
    boolean isBadgeAchieved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_score);

        DayNight dayNight = new DayNight(this);

        dayNight.checkContentView(binding.contentView);

        Global.networkCheck(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        userSelectedOptions = getIntent().getStringArrayListExtra("userSelectedOptions");
        score = getIntent().getIntExtra("score",0);
        level = getIntent().getStringExtra("level");
        skippedQuestions = getIntent().getIntExtra("skippedQuestions",0);
        totalQuestions = getIntent().getIntExtra("totalQuestions",0);
        unviwedQuestions = getIntent().getIntExtra("unviwedQuestions",0);
        int totalCorrectAnswers = score/10;
        int attemptedQuestions = totalQuestions-skippedQuestions;
        int completionPercent = (totalQuestions-unviwedQuestions)*100/totalQuestions;
        int incorrectAnswers = attemptedQuestions-totalCorrectAnswers;
        int passMarks = 60;

        Global.statusbarAccentColor(this);


        if (totalCorrectAnswers*10>=passMarks){
            binding.tvComment.setText(R.string.good_job_msg);
            binding.loseAnimeStatus.setVisibility(View.GONE);
            binding.winAnimeStatus.setVisibility(View.VISIBLE);
            isBadgeAchieved = true;

            FirebaseDatabase.getInstance().getReference(Global.USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.hasChild(level)){
                                FirebaseDatabase.getInstance().getReference(Global.USERS)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(level).setValue("achieved");
                                isFirstTime = true;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(),getString(R.string.network_error),Toast.LENGTH_SHORT).show();
                            Log.d("ScoreActivity", "onCancelled: ");
                        }
                    });

            if (level.equals(Global.LEVEL_1)){
                binding.ivBadge.setImageResource(R.drawable.level_1);
                setProKeyInLocalDB(false);
            }else if (level.equals(Global.LEVEL_2)){
                binding.ivBadge.setImageResource(R.drawable.level_2);
                setProKeyInLocalDB(false);
            }else if (level.equals(Global.LEVEL_3)){
                binding.ivBadge.setImageResource(R.drawable.level_3);
                setProKeyInLocalDB(false);
            }else if (level.equals(Global.LEVEL_4)){
                binding.ivBadge.setImageResource(R.drawable.level_4);
                setProKeyInLocalDB(false);
            }else if (level.equals(Global.LEVEL_5)){
                binding.ivBadge.setImageResource(R.drawable.level_5);
                setProKeyInLocalDB(true);
            }
        }else {
            binding.tvComment.setText(R.string.better_luck_msg);
            binding.winAnimeStatus.setVisibility(View.GONE);
            binding.loseAnimeStatus.setVisibility(View.VISIBLE);
        }

        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("isBadgeAchieved",isBadgeAchieved);
                intent.putExtra("isFirstTime",isFirstTime);
                intent.putExtra("level",level);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnToChkAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QuizActivity.class)
                .putExtra("userSelectedOptions",userSelectedOptions)
                .putExtra("quiz.score.solutions",true)
                .putExtra("quizLevel",level));
            }
        });

        binding.tvCorrectAnswers.setText(totalCorrectAnswers+getString(R.string.questions_msg));
        binding.tvSkipped.setText(String.valueOf(skippedQuestions));
        binding.tvCompletion.setText(completionPercent+"%");
        binding.tvIncorrect.setText(String.valueOf(incorrectAnswers));
        binding.tvPointDesc.setText(getString(R.string.you_got_msg)+score+getString(R.string.quiz_points_msg));




    }

    void setProKeyInLocalDB(boolean isPro){

        SharedPreferences pref = this.getSharedPreferences(getPackageName(),MODE_PRIVATE);
        pref.edit().putBoolean(Global.proKey,isPro).apply();

    }
}