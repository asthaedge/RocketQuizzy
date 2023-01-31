package com.rocket.quizzy.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.rocket.quizzy.databinding.ActivityFullImageBinding;
import com.rocket.quizzy.service.FirebaseService;
import com.rocket.quizzy.service.interfaces.FireResult;
import com.rocket.quizzy.view.auth.SetUserInfoActivity;
import com.rocket.quizzy.view.custom.DialogInterface;
import com.rocket.quizzy.view.custom.Loading_Dialog;

import java.io.IOException;

public class FullImageActivity extends AppCompatActivity {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    ActivityFullImageBinding binding;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_full_image);
        reference = FirebaseDatabase.getInstance().getReference();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Global.networkCheck(this);
        imageUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(FullImageActivity.this).load(imageUrl).into(binding.imageView);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra(Global.REQUEST_QUIT_KEY,Global.PROFILE_REQUEST);
                startActivity(i);
                Animatoo.animateZoom(FullImageActivity.this);
                finish();
            }
        });

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(FullImageActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), getString(R.string.permit_notice), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra(Global.REQUEST_QUIT_KEY,Global.PROFILE_REQUEST);
        startActivity(i);
        Animatoo.animateZoom(FullImageActivity.this);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_GALLERY_REQUEST){
                Uri mediauri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mediauri);
                    binding.imageView.setImageBitmap(bitmap);
                    if (mediauri!=null){
                        Loading_Dialog loading_dialog = new Loading_Dialog(FullImageActivity.this);
                        loading_dialog.startLoadingDialog(new DialogInterface() {
                            @Override
                            public void onLoadingProgressBar(Dialog dialog) {
                                RoundCornerProgressBar progressBar = dialog.findViewById(R.id.hori_progress_Bar);
                                TextView percent = dialog.findViewById(R.id.tv_percent);
                                FirebaseService firebaseService= new FirebaseService(FullImageActivity.this);
                                firebaseService.uploadImagetoFirebaseStorage(mediauri, progressBar, percent, new FireResult() {
                                    @Override
                                    public void onUploadSuccess(String image) {
                                        reference.child(Global.USERS).child(firebaseUser.getUid())
                                                .child("imageUri").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Glide.with(FullImageActivity.this).load(image).listener(new RequestListener<Drawable>() {
                                                    @Override
                                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                        Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                                        return true;
                                                    }

                                                    @Override
                                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                        loading_dialog.dismissDialog();
                                                        Snackbar.make(binding.contentView, R.string.profile_updated_msg,Snackbar.LENGTH_SHORT).show();
                                                        return true;
                                                    }
                                                }).into(binding.imageView);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loading_dialog.dismissDialog();
                                                Toast.makeText(FullImageActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onUploadFailure(String error) {
                                        loading_dialog.dismissDialog();
                                        Toast.makeText(FullImageActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }else {
                        Toast.makeText(FullImageActivity.this, getString(R.string.sth_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}