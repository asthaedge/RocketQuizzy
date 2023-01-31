package com.rocket.quizzy.view.auth;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.ActivityLoginBinding;
import com.rocket.quizzy.service.DayNight;

import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        Global.statusbarAccentColor(this);
        DayNight dayNight = new DayNight(this);
        dayNight.checkContentView(binding.rlLayout);
        dayNight.checkTextView(binding.tvDesc,R.color.grey);
        dayNight.checkCardView(binding.cardView);
        dayNight.checkEditText(binding.edtvPhoneNo);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Global.networkCheck(this);

        Dexter.withActivity(this).withPermission(Manifest.permission.RECORD_AUDIO).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        });
        binding.btnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnId.startAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String phoneNo = binding.edtvPhoneNo.getText().toString();
                        if (validatePhoneNo(phoneNo)){
                            phoneNo = binding.contrycodepicker.getSelectedCountryCodeWithPlus()+phoneNo;
                            Intent i = new Intent(getApplicationContext(),OTPVerifyActivity.class);

                            i.putExtra("phoneNo",phoneNo);

                            startActivity(i);
                            Animatoo.animateZoom(LoginActivity.this);
                            finish();
                        }else {
                            binding.btnId.revertAnimation();
                            binding.btnId.setBackground(getDrawable(R.drawable.button_bg));
                            return;
                        }
                    }
                },3000);

            }
        });
    }

    /*
    Validation Functions
     */

    private boolean validatePhoneNo(String phoneNo){

        if (phoneNo.length()!=10){
            binding.edtvPhoneNo.setError("Error Phone Number!");
            return false;
        }else if (phoneNo.equals("")){
            binding.edtvPhoneNo.setError("Empty Phone Number!");
            return false;
        }else if (phoneNo.equals("[a-zA-Z ]+")){
            binding.edtvPhoneNo.setError("Whitespaces Not Allowed!");
            return false;
        }else {
            binding.edtvPhoneNo.setError(null);
            return true;
        }

    }

}