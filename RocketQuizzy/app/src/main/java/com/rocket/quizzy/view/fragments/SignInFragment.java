package com.rocket.quizzy.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.FragmentSignInBinding;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.auth.LoginActivity;

import java.util.Objects;

public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }

    FragmentSignInBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_in, container, false);

        DayNight dayNight = new DayNight(requireActivity().getApplicationContext());
        dayNight.checkContentView(binding.contentView);

        binding.loginBtn.setOnClickListener(v-> {
            startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return binding.getRoot();
    }
}