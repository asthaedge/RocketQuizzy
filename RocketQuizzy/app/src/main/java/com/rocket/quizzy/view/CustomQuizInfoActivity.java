package com.rocket.quizzy.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.databinding.ActivityCustomQuizInfoBinding;
import com.rocket.quizzy.model.Question;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.custom.Loading_Dialog;

import java.util.ArrayList;

public class CustomQuizInfoActivity extends AppCompatActivity {

    ActivityCustomQuizInfoBinding binding;
    String quizTitle;
    String quizDesc;
    Bitmap thumbnail;
    String quizID;
    ArrayList<String> favList;
    Loading_Dialog loading;
    boolean isCurrentItemLiked;
    private boolean isCameFromHomeFragment = false;
    private boolean isCameFromFavFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_custom_quiz_info);
        DayNight dayNight = new DayNight(this);

        dayNight.checkContentView(binding.contentView);
        dayNight.checkTextView(binding.tvDesc,R.color.grey);

        Intent intent = getIntent();
        if (intent.hasExtra("isCameFromHomeFragment")){
            isCameFromHomeFragment = intent.getBooleanExtra("isCameFromHomeFragment",false);
        }
        if (intent.hasExtra("fromFavFragment")){
            isCameFromFavFragment = intent.getBooleanExtra("fromFavFragment",false);
        }

        loading = new Loading_Dialog(this);

        quizTitle = getIntent().getStringExtra("qtitle");
        quizDesc = getIntent().getStringExtra("qDesc");
        quizID = getIntent().getStringExtra("qID");
        byte[] byteArray = getIntent().getByteArrayExtra("thumbnail");
        thumbnail = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        binding.ivThumbnail.setImageBitmap(thumbnail);

        binding.tvTitle.setText(quizTitle);
        binding.tvDesc.setText(quizDesc);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Global.USERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Global.FAVOURITE);

        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        favList.clear();
                        if (snapshot.exists()){
                            for (DataSnapshot ds : snapshot.getChildren()){
                                String favID = ds.child(Global.FAV_ID).getValue().toString();
                                favList.add(favID);
                            }
                            if (favList.contains(quizID)){
                                binding.favButton.setImageDrawable(getDrawable(R.drawable.ic_heart_filled));
                                isCurrentItemLiked = true;
                            }else {
                                binding.favButton.setImageDrawable(getDrawable(R.drawable.ic_heart));
                                isCurrentItemLiked = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomQuizInfoActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.backBtn.setOnClickListener(v -> {
            if (isCameFromHomeFragment){
                setResult(RESULT_OK);
            }else if (isCameFromFavFragment){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra(Global.REQUEST_QUIT_KEY,Global.FAV_REQUEST);
                startActivity(i);
                Animatoo.animateZoom(CustomQuizInfoActivity.this);
            }
            finish();
        });

        binding.favButton.setOnClickListener(v -> {
            if (isCurrentItemLiked){
                loading.startLoadingDialog();
                reference.child(quizID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.favButton.setImageDrawable(getDrawable(R.drawable.ic_heart));
                        isCurrentItemLiked = false;
                        loading.dismissDialog();
                        Snackbar.make(binding.contentView, R.string.removed_from_fav_msg,Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismissDialog();
                        Snackbar.make(binding.contentView,getString(R.string.sth_went_wrong),Snackbar.LENGTH_SHORT).show();
                    }
                });
            }else {
                reference.child(quizID).child(Global.FAV_ID).setValue(quizID).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.favButton.setImageDrawable(getDrawable(R.drawable.ic_heart_filled));
                        isCurrentItemLiked = true;
                        loading.dismissDialog();
                        Snackbar.make(binding.contentView, R.string.added_to_fav_msg,Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismissDialog();
                        Snackbar.make(binding.contentView,getString(R.string.sth_went_wrong),Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.playBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),QuizActivity.class)
                    .putExtra("qID",quizID)
                    .putExtra("quizLevel",Global.PUBLIC_LEVEL_KEY));
        });


    }

    @Override
    public void onBackPressed() {
        if (isCameFromHomeFragment){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra(Global.REQUEST_QUIT_KEY,Global.HOME_REQUEST);
            startActivity(i);
            Animatoo.animateZoom(CustomQuizInfoActivity.this);
        }else if (isCameFromFavFragment){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra(Global.REQUEST_QUIT_KEY,Global.FAV_REQUEST);
            startActivity(i);
            Animatoo.animateZoom(CustomQuizInfoActivity.this);
        }
        finish();
    }
}