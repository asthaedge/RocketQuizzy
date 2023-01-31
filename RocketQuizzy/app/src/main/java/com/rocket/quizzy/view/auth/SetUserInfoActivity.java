package com.rocket.quizzy.view.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.ActivitySetUserInfoBinding;
import com.rocket.quizzy.model.User;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.service.FirebaseService;
import com.rocket.quizzy.service.interfaces.FireResult;
import com.rocket.quizzy.view.QuizInfoActivity;
import com.rocket.quizzy.view.custom.DialogInterface;
import com.rocket.quizzy.view.custom.Loading_Dialog;

import java.io.IOException;

public class SetUserInfoActivity extends AppCompatActivity {

    ActivitySetUserInfoBinding binding;
    String imageUrl;
    private int IMAGE_GALLERY_REQUEST = 1;
    Uri mediauri;
    String phoneNo;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_set_user_info);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DayNight dayNight = new DayNight(this);
        dayNight.checkContentView(binding.rlLayout);
        dayNight.checkCardView(binding.cardView);
        dayNight.checkEditText(binding.edtvName);

        Global.networkCheck(this);

        database = FirebaseDatabase.getInstance().getReference(Global.USERS);
        imageUrl = "";
        phoneNo = getIntent().getStringExtra("phoneNo");

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(SetUserInfoActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "Without Storage Permit, We cannot add profile!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

            }
        });

        binding.btnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.edtvName.getText().toString();
                if (validateName(name)){
                    if (mediauri!=null){
                        Loading_Dialog loading_dialog = new Loading_Dialog(SetUserInfoActivity.this);
                        loading_dialog.startLoadingDialog(new DialogInterface() {
                            @Override
                            public void onLoadingProgressBar(Dialog dialog) {
                                RoundCornerProgressBar progressBar = dialog.findViewById(R.id.hori_progress_Bar);
                                TextView percent = dialog.findViewById(R.id.tv_percent);
                                FirebaseService firebaseService= new FirebaseService(SetUserInfoActivity.this);
                                firebaseService.uploadImagetoFirebaseStorage(mediauri, progressBar, percent, new FireResult() {
                                    @Override
                                    public void onUploadSuccess(String imageUrlLocal) {
                                        imageUrl = imageUrlLocal;
                                        submitBtnActions(loading_dialog);
                                    }

                                    @Override
                                    public void onUploadFailure(String error) {
                                        Toast.makeText(getApplicationContext(), "Bad Network!", Toast.LENGTH_SHORT).show();
                                        submitBtnActions(loading_dialog);
                                    }
                                });
                            }
                        });

                    }else {
                        Loading_Dialog loading_dialog = new Loading_Dialog(SetUserInfoActivity.this);
                        loading_dialog.startLoadingDialog();
                        submitBtnActions(loading_dialog);
                    }
                }else {
                    setButtonNormal();
                    return;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
    }

    private void submitBtnActions(Loading_Dialog loading_dialog){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = binding.edtvName.getText().toString();
        User user = new User(name,
                phoneNo,
                imageUrl,
                uid);

        database.child(uid).child("name").setValue(user.getName()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                database.child(uid).child("phoneNo").setValue(user.getPhoneNo()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.child(uid).child("imageUri").setValue(user.getImageUri()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.child(uid).child("uid").setValue(user.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loading_dialog.dismissDialog();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading_dialog.dismissDialog();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Bad Connection!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
    }

    /*
    Validation Function
     */

    private boolean validateName(String name){
        if (name.equals("")){
            binding.edtvName.setError("Field can not be empty!");
            return false;
        }else if (name.length()>15){
            binding.edtvName.setError("Name cannot be greater than 15 words!");
            return false;
        }else if (name.length()<5){
            binding.edtvName.setError("Name cannot be less than 5 words!");
            return false;
        }else {
            binding.edtvName.setError(null);
            return true;
        }
    }

    void setButtonNormal(){
        binding.btnId.revertAnimation();
        binding.btnId.setBackground(getDrawable(R.drawable.button_bg));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_GALLERY_REQUEST){
                mediauri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mediauri);
                    binding.ivProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}