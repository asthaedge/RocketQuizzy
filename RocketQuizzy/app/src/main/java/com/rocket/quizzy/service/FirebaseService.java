package com.rocket.quizzy.service;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rocket.quizzy.service.interfaces.FireResult;

public class FirebaseService {

    Context context;
    FirebaseStorage firebaseStorage;

    public FirebaseService(Context context) {
        this.context = context;
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public void uploadImagetoFirebaseStorage(Uri uri, RoundCornerProgressBar progressBar, TextView tvProgress, final FireResult onCallBack){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImageProfiles/" + uid + "/" + System.currentTimeMillis() + "." + getFileExtention(uri));
            riversRef.putFile(uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    float progress = (100*snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress(progress);
                    tvProgress.setText(String.valueOf(progress)+"%");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();

                    final String sdownload_url = String.valueOf(downloadUrl);

                    onCallBack.onUploadSuccess(sdownload_url);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onCallBack.onUploadFailure(e.getLocalizedMessage());
                }
            });
        }
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
