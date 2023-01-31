package com.rocket.quizzy.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;
import com.rocket.quizzy.adapter.FavAdapter;
import com.rocket.quizzy.databinding.FragmentFavBinding;
import com.rocket.quizzy.model.Quiz;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.custom.Loading_Dialog;

import java.util.ArrayList;
import java.util.List;


public class FavFragment extends Fragment {


    public FavFragment() {
        // Required empty public constructor
    }

    FragmentFavBinding binding;
    List<Quiz> list = new ArrayList<>();
    ArrayList<String> favIDs = new ArrayList<>();
    FavAdapter favAdapter;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    Loading_Dialog loading_dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_fav, container, false);

        DayNight dayNight = new DayNight(getActivity());
        dayNight.checkContentView(binding.contentView);
        dayNight.checkToolbar(binding.toolbar);
        dayNight.checkImageView(binding.ivR);
        dayNight.checkTextView(binding.tvToolbarTitle,R.color.themeColor);

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        loading_dialog = new Loading_Dialog(getActivity());

        loading_dialog.startLoadingDialog();

        loadFavList();

        return binding.getRoot();
    }

    private void loadFavList() {

        reference.child(Global.USERS).child(firebaseUser.getUid()).child(Global.FAVOURITE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        favIDs.clear();
                        list.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String favID = ds.child(Global.FAV_ID).getValue().toString();
                            favIDs.add(favID);
                        }

                        loadList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), getText(R.string.sth_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadList() {
        for (String favID : favIDs){
            reference.child(Global.QUIZ).child(favID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Quiz quiz = snapshot.getValue(Quiz.class);
                    list.add(quiz);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity().getApplicationContext(), getText(R.string.sth_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (list.size()>0){
            loading_dialog.dismissDialog();
            binding.emptyLayout.setVisibility(View.GONE);
            binding.favRecycler.setVisibility(View.VISIBLE);
            FavAdapter favAdapter = new FavAdapter(list,getActivity().getApplicationContext(),getActivity());
            binding.favRecycler.setAdapter(favAdapter);
        }else {
            loading_dialog.dismissDialog();
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.favRecycler.setVisibility(View.GONE);
        }

    }
}