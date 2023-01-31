package com.rocket.quizzy.boardingScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.FragmentOnBoarding3Binding;
import com.rocket.quizzy.view.auth.LoginActivity;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OnBoardingFragment_3 extends Fragment {


    public OnBoardingFragment_3() {
        // Required empty public constructor
    }

    FragmentOnBoarding3Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_boarding_3, container, false);


         binding.nextBtn.setOnClickListener(view -> {
             startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
             getActivity().finish();
         });

        return binding.getRoot();
    }
}