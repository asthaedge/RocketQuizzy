package com.rocket.quizzy.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.adapter.CategoryAdapter;
import com.rocket.quizzy.adapter.QuizAdapter;
import com.rocket.quizzy.adapter.SliderAdapter;
import com.rocket.quizzy.databinding.FragmentHomeBinding;
import com.rocket.quizzy.model.Category;
import com.rocket.quizzy.model.Quiz;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.AllCategoriesActivity;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    List<Quiz> topList;
    List<Quiz> searchList;
    QuizAdapter quizAdapter;
    QuizAdapter searchQuizAdapter;
    List<Category> categoryList;
    CategoryAdapter categoryAdapter;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FragmentHomeBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        binding.categoryRV.setExpanded(true);

        DayNight dayNight = new DayNight(getActivity());
        dayNight.checkContentView(binding.contentView);
        dayNight.checkCardView(binding.searchLayout);
        dayNight.checkEditText(binding.searchBar);

        topList = new ArrayList<>();
        categoryList = new ArrayList<>();
        searchList = new ArrayList<>();
        quizAdapter = new QuizAdapter(topList, getActivity().getApplicationContext(), true,getActivity());
        categoryAdapter = new CategoryAdapter(getActivity().getApplicationContext(),getActivity(),categoryList);
        searchQuizAdapter = new QuizAdapter(searchList, getActivity().getApplicationContext(), false,getActivity());

        binding.categoryRV.setAdapter(categoryAdapter);
        binding.topItem.setAdapter(quizAdapter);

        reference = FirebaseDatabase.getInstance().getReference();

        loadTopItems();
        loadCategories();
        searchList();

        binding.viewAllBtn.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity().getApplicationContext(), AllCategoriesActivity.class), MainActivity.REQUEST_RETURN_HOME);
        });

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.searchBar.getText().toString().isEmpty()){
                    binding.searchLayout.setVisibility(View.GONE);
                    binding.topLayout.setVisibility(View.VISIBLE);
                    binding.categoryLayout.setVisibility(View.VISIBLE);
                }else {
                    binding.searchLayout.setVisibility(View.VISIBLE);
                    binding.topLayout.setVisibility(View.GONE);
                    binding.categoryLayout.setVisibility(View.GONE);
                    filter(binding.searchBar.getText().toString());
                }
            }
        });

        return binding.getRoot();
    }

    private void searchList() {
        reference.child(Global.QUIZ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchList.clear();
                if (snapshot.hasChild(Global.LEVEL_1)) {
                } else if (snapshot.hasChild(Global.LEVEL_2)) {
                } else if (snapshot.hasChild(Global.LEVEL_3)) {
                } else if (snapshot.hasChild(Global.LEVEL_4)) {
                } else if (snapshot.hasChild(Global.LEVEL_5)) {
                } else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Quiz quiz = ds.getValue(Quiz.class);

                        searchList.add(quiz);

                    }
                    searchQuizAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {

        List<Quiz> filteredList = new ArrayList<>();

        for (Quiz quiz:searchList){
            if (quiz.getTags().toString().contains(text) || quiz.getQuizTitle().contains(text)){
                filteredList.add(quiz);
            }
        }

        searchQuizAdapter.filterList(filteredList,binding.searchRecycler,binding.blankRecyclerTv);

    }

    private void loadTopItems() {
        reference.child(Global.QUIZ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topList.clear();
                if (snapshot.hasChild(Global.LEVEL_1)) {
                } else if (snapshot.hasChild(Global.LEVEL_2)) {
                } else if (snapshot.hasChild(Global.LEVEL_3)) {
                } else if (snapshot.hasChild(Global.LEVEL_4)) {
                } else if (snapshot.hasChild(Global.LEVEL_5)) {
                } else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Quiz quiz = ds.getValue(Quiz.class);

                        if (quiz.isTop()) {
                            topList.add(quiz);
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadCategories() {

        reference.child(Global.CATEGORIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (categoryList.size() < 6) {
                        Category category = ds.getValue(Category.class);
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });

    }

}