package com.rocket.quizzy.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.ActivityOtpverifyBinding;
import com.rocket.quizzy.service.DayNight;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTPVerifyActivity extends AppCompatActivity {

    TextView tv_desc;
    ActivityOtpverifyBinding binding;

    private FirebaseAuth mAuth;
    PhoneAuthCredential credential;
    String codeBySystem;
    long duration = 60*1000;
    long tick = 1000;
    int count=0;
    private static String TAG = "OTPVerify";
    String phoneNo;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_otpverify);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Global.networkCheck(this);
        DayNight dayNight = new DayNight(this);
        dayNight.checkContentView(binding.rlLayout);

        tv_desc = findViewById(R.id.tv_desc);

        Global.statusbarAccentColor(this);
        phoneNo = getIntent().getStringExtra("phoneNo");

        if (phoneNo!=null){
            tv_desc.setText("Enter the OTP sent on the number "+phoneNo);
        }

        mAuth = FirebaseAuth.getInstance();

        sendVerificationCodeToUser(phoneNo);
        binding.rendOtp.setVisibility(View.GONE);
        binding.tick.setVisibility(View.VISIBLE);
        new CountDownTimer(duration, tick) {

            public void onTick(long millisUntilFinished) {
                binding.tick.setText(timeSec(count));
                count= count+1;
            }

            public void onFinish() {
                binding.rendOtp.setVisibility(View.VISIBLE);
                binding.tick.setVisibility(View.GONE);
                count=0;
            }
        }.start();

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.errorLayout.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(binding.pinView.getText().toString().trim())) {
                    verifyCode(binding.pinView.getText().toString());

                } else
                {
                    setPinViewError("Empty OTP");
                }
            }
        });
        binding.btnSubmit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                binding.errorLayout.setVisibility(View.GONE);
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (!TextUtils.isEmpty(binding.pinView.getText().toString().trim())) {
                                verifyCode(binding.pinView.getText().toString().trim());
                            } else
                            {
                                setPinViewError("Empty OTP");
                            }
                            return true;
                        default:
                            break;
                    }

                }
                return false;
            }
        });
        binding.rendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rendOtp.setVisibility(View.GONE);
                binding.tick.setVisibility(View.VISIBLE);

                sendVerificationCodeToUser(phoneNo);
                new CountDownTimer(duration, tick) {

                    public void onTick(long millisUntilFinished) {
                        binding.tick.setText(timeSec(count));
                        count= count+1;
                    }

                    public void onFinish() {
                        binding.rendOtp.setVisibility(View.VISIBLE);
                        binding.tick.setVisibility(View.GONE);
                        count=0;
                    }
                }.start();
            }
        });
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                OTPVerifyActivity.this,// Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent (s, forceResendingToken);
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();

                    if (code != null) {
                        binding.pinView.setText(code);
                        //verifying the code
                        verifyCode(code);
                    }

                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    setPinViewError("Wrong OTP");
                }
            };

    private void verifyCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        //signing the user
        if(credential != null)
            signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            startActivity(new Intent(getApplicationContext(), SetUserInfoActivity.class)
                            .putExtra("phoneNo",phoneNo));
                            Animatoo.animateZoom(OTPVerifyActivity.this);
                            finish();

                        }

                    }
                });
    }

    public static String timeSec(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds - minutes * 60;

        String formattedTime = "";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds;

        return formattedTime;
    }

    public void backotpcross(View view) {
        onBackPressed();
    }

    void setPinViewError(String message){
        binding.errorLayout.setText("Wrong OTP");
        binding.errorLayout.setVisibility(View.VISIBLE);
        binding.btnSubmit.revertAnimation();
        binding.btnSubmit.setBackground(getDrawable(R.drawable.button_bg));
    }
}