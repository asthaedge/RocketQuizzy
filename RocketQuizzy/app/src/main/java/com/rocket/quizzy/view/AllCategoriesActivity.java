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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;
import com.rocket.quizzy.adapter.CategoryAdapter;
import com.rocket.quizzy.databinding.ActivityAllCategoriesBinding;
import com.rocket.quizzy.model.Category;
import com.rocket.quizzy.service.DayNight;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllCategoriesActivity extends AppCompatActivity {

    ActivityAllCategoriesBinding binding;
    List<Category> list = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    private List<Category> categoryItemsSearch = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_all_categories);

        binding.categoryRV.setExpanded(true);

        categoryAdapter = new CategoryAdapter(AllCategoriesActivity.this,AllCategoriesActivity.this,list);
        binding.categoryRV.setAdapter(categoryAdapter);
        DayNight dayNight = new DayNight(this);

        dayNight.checkContentView(binding.contentView);
        dayNight.checkImageView(binding.backBtn);
        dayNight.checkTextView(binding.tvToolbarTitle,R.color.themeColor);
        dayNight.checkToolbar(binding.toolbar);
        dayNight.checkCardView(binding.searchLayout);
        dayNight.checkEditText(binding.searchBar);

        loadCategories();

        binding.backBtn.setOnClickListener(view -> {
            Intent i = new Intent();
            i.putExtra(Global.REQUEST_QUIT_KEY,Global.HOME_REQUEST);
            setResult(RESULT_OK,i);
            finish();
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
                filter(binding.searchBar.getText().toString());
            }
        });
    }

    private void filter(String text) {

        if (text.length() > 0) {
            list.clear();
            for (Category c : categoryItemsSearch) {
                String ss = c.getName().toLowerCase();
                if (ss.contains(text.toLowerCase()) || text.toLowerCase().contains(ss.toLowerCase())) {
                    list.add(c);
                }
            }
            categoryAdapter.notifyDataSetChanged();
        } else {
            list.clear();
            list.addAll(categoryItemsSearch);
            categoryAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra(Global.REQUEST_QUIT_KEY,Global.HOME_REQUEST);
        setResult(RESULT_OK,i);
        finish();
    }

    private void loadCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(Global.CATEGORIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                categoryItemsSearch.clear();
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Category category = ds.getValue(Category.class);
                        list.add(category);
                        categoryItemsSearch.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();

                    if (list.size()>0){
                        binding.categoryRV.setVisibility(View.VISIBLE);
                        binding.blankRecyclerTv.setVisibility(View.GONE);
                    }else {
                        binding.categoryRV.setVisibility(View.GONE);
                        binding.blankRecyclerTv.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllCategoriesActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}