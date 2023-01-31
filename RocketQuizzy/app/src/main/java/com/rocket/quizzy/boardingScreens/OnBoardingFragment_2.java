package com.rocket.quizzy.boardingScreens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.rocket.quizzy.R;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OnBoardingFragment_2 extends Fragment {

    public OnBoardingFragment_2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding_2, container, false);

        return root;
    }
}