package com.rocket.quizzy.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.adapter.QuizAdapter;
import com.rocket.quizzy.databinding.ActivityQuizListBinding;
import com.rocket.quizzy.model.Quiz;
import com.rocket.quizzy.service.DayNight;

import java.util.ArrayList;
import java.util.List;

public class QuizListActivity extends AppCompatActivity {

    ActivityQuizListBinding binding;
    QuizAdapter quizAdapter;
    DatabaseReference reference;
    List<Quiz> list;
    String categoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_list);

        DayNight dayNight = new DayNight(this);

        dayNight.checkContentView(binding.contentView);
        dayNight.checkImageView(binding.backBtn);
        dayNight.checkTextView(binding.tvCategoryName,R.color.themeColor);
        dayNight.checkToolbar(binding.toolbar);
        dayNight.checkCardView(binding.searBarLayout);
        dayNight.checkEditText(binding.searchBar);

        reference = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        quizAdapter = new QuizAdapter(list, getApplicationContext(),false,QuizListActivity.this);


        categoryID = getIntent().getStringExtra("categoryID");
        String categoryName = getIntent().getStringExtra("categoryName");

        binding.tvCategoryName.setText(categoryName);
        binding.searchBar.setHint(getString(R.string.search_in_msg) + categoryName);

        getQuizList();

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(binding.searchBar.getText().toString());
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra(Global.REQUEST_QUIT_KEY,Global.HOME_REQUEST);
            startActivity(i);
            Animatoo.animateZoom(QuizListActivity.this);
            finish();
        });


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra(Global.REQUEST_QUIT_KEY,Global.HOME_REQUEST);
        startActivity(i);
        Animatoo.animateZoom(QuizListActivity.this);
        finish();
    }

    private void filter(String text) {

        List<Quiz> filteredList = new ArrayList<>();

        for (Quiz quiz : list) {
            if (quiz.getQuizTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(quiz);
            }
        }

        quizAdapter.filterList(filteredList, binding.quizRecycler, binding.blankRecyclerTv);

    }

    private void getQuizList() {

        reference.child(Global.QUIZ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.hasChild(Global.LEVEL_1)) {
                } else if (snapshot.hasChild(Global.LEVEL_2)) {
                } else if (snapshot.hasChild(Global.LEVEL_3)) {
                } else if (snapshot.hasChild(Global.LEVEL_4)) {
                } else if (snapshot.hasChild(Global.LEVEL_5)) {
                } else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Quiz quiz = ds.getValue(Quiz.class);

                        if (quiz.getCategoryId().equals(categoryID)){
                            list.add(quiz);
                        }
                    }
                    binding.tvCountQuizzes.setVisibility(View.VISIBLE);
                    binding.tvCountQuizzes.setText(getString(R.string.showing_msg) + list.size() + getString(R.string.quizzes_msg));
                    if (list.size() == 0) {
                        binding.quizRecycler.setVisibility(View.GONE);
                        binding.blankRecyclerTv.setVisibility(View.VISIBLE);
                    } else {
                        binding.quizRecycler.setVisibility(View.VISIBLE);
                        binding.blankRecyclerTv.setVisibility(View.GONE);
                    }
                    quizAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });

    }
}