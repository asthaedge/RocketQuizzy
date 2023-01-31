package com.rocket.quizzy.view.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;

import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.FragmentSettingsBinding;
import com.rocket.quizzy.model.Languages;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.startup.SplashScreenActivity;

import java.util.Locale;
import java.util.stream.Stream;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public SettingsFragment() {
        // Required empty public constructor
    }

    FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false);

        DayNight dayNight = new DayNight(getActivity());
        dayNight.checkContentView(binding.contentView);
        dayNight.checkToolbar(binding.toolbar);
        dayNight.checkImageView(binding.ivR);
        dayNight.checkTextView(binding.tvToolbarTitle,R.color.themeColor);
        dayNight.checkTextView(binding.tvText1,R.color.grey);
        dayNight.checkTextView(binding.tvText2,R.color.grey);
        dayNight.checkTextView(binding.tvText3,R.color.grey);
        dayNight.checkTextView(binding.tvCurrentLanguage,R.color.grey);
        dayNight.checkCardView(binding.languageLayout);

        initializeLanguages();

        if (Global.isDarkMode(getActivity().getApplicationContext())){
            binding.ivDarkMode.setBackgroundResource(R.drawable.theme_btn_selected_bg);
            binding.ivLightMode.setBackgroundResource(R.color.white);
        }else {
            binding.ivLightMode.setBackgroundResource(R.drawable.theme_btn_selected_bg);
            binding.ivDarkMode.setBackgroundResource(R.color.darkLight);
        }

        binding.darkModeBtn.setOnClickListener(v -> {
            if (!Global.isDarkMode(getActivity().getApplicationContext())){
                Global.setDarkMode(getActivity(),true);
                startActivity(new Intent(getActivity().getApplicationContext(), SplashScreenActivity.class));
                getActivity().finish();
            }
        });

        binding.lightModeBtn.setOnClickListener(v -> {
            if (Global.isDarkMode(getActivity().getApplicationContext())){
                Global.setDarkMode(getActivity(),false);
                startActivity(new Intent(getActivity().getApplicationContext(), SplashScreenActivity.class));
                getActivity().finish();
            }
        });

        binding.stopNotifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Global.setNotification(getActivity(),true);
                }else {
                    Global.setNotification(getActivity(),false);
                }
            }
        });

        return binding.getRoot();
    }

    private void initializeLanguages() {
        binding.tvCurrentLanguage.setText(getString(R.string.current_lang_msg)+" :-"+Global.getLanguage(getActivity()));
        binding.languageLayout.setVisibility(View.VISIBLE);

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_spinner_dropdown_item, new String[]{"Select Your Language","English (US)","Hindi"}){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                CheckedTextView tv = (CheckedTextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor((Global.isDarkMode(getActivity().getApplicationContext())) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.grey));
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(spinnerArrayAdapter);

        binding.spinner.setOnItemSelectedListener(SettingsFragment.this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position!=0){
            String[] languages = {"none","English (US)", "Hindi"};
            String selectedLang = languages[position];
            Global.setLanguage(getActivity().getApplicationContext(),selectedLang);
            changeLanguage(Languages.getLanguageKey(selectedLang));
        }
    }

    private void changeLanguage(String selectedLang) {
        Locale locale = new Locale(selectedLang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());

        startActivity(new Intent(getActivity().getApplicationContext(),SplashScreenActivity.class));
        getActivity().finish();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}