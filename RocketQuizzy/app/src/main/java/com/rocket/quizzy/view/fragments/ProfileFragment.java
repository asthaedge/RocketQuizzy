package com.rocket.quizzy.view.fragments;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.adapter.BadgeAdapter;
import com.rocket.quizzy.databinding.FragmentProfileBinding;
import com.rocket.quizzy.model.Badge;
import com.rocket.quizzy.model.User;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.FullImageActivity;
import com.rocket.quizzy.view.auth.LoginActivity;
import com.rocket.quizzy.view.custom.Alert_Dialog;
import com.rocket.quizzy.view.custom.Loading_Dialog;
import com.rocket.quizzy.view.custom.listeners.Alert_Listener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    FragmentProfileBinding binding;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    List<Badge> list;
    BadgeAdapter adapter;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false);

        DayNight dayNight = new DayNight(getActivity());
        dayNight.checkContentView(binding.contentView);
        dayNight.checkToolbar(binding.toolbar);
        dayNight.checkImageView(binding.ivR);
        dayNight.checkTextView(binding.tvToolbarTitle,R.color.themeColor);
        dayNight.checkTextView(binding.tv1,R.color.grey);
        dayNight.checkTextView(binding.tvDesc,R.color.grey);
        dayNight.checkCardView(binding.nameLayout);
        dayNight.checkCardView(binding.numberLayout);
        dayNight.checkEditText(binding.edtvName);
        dayNight.checkEditText(binding.edtvPhoneNo);
        dayNight.checkLogoutButton(binding.logoutBtn,binding.tvLogout,binding.ivLogout);


        reference = FirebaseDatabase.getInstance().getReference(Global.USERS);
        reference.keepSynced(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        loadBadgeRecycler();
        loadData();
        onClicks();

        if (Global.isPro(getActivity())){
            setUIasUserType(true);
        }else {
            setUIasUserType(false);
        }

        return binding.getRoot();
    }

    private void onClicks() {
        binding.btnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnId.startAnimation();
                if (validateName(binding.edtvName.getText().toString())){

                    reference.child(firebaseUser.getUid()).child("name")
                            .setValue(binding.edtvName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    setButtonNormal();
                                    Snackbar.make(binding.contentView,getString(R.string.name_update_message),Snackbar.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    setButtonNormal();
                                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                }
                            });

                }else {
                    setButtonNormal();
                    return;
                }
            }
        });

        binding.logoutBtn.setOnClickListener(v ->{
            Alert_Dialog alert_dialog = new Alert_Dialog(getActivity(),
                    getString(R.string.log_out),
                    getString(R.string.log_out_dialog_desc));
            alert_dialog.showAlert(new Alert_Listener() {
                @Override
                public void onYesClick(Dialog dialog) {
                    Loading_Dialog loading_dialog = new Loading_Dialog(getActivity());
                    loading_dialog.startLoadingDialog();
                    FirebaseAuth.getInstance().signOut();
                    boolean isFirstScreenLogin = Global.isisFirstScreenLogin(getActivity().getApplicationContext());
                    if (isFirstScreenLogin){
                        startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
                    }else {
                        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                    }
                    getActivity().finish();
                    dialog.dismiss();
                }

                @Override
                public void onNoClick(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        });

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), FullImageActivity.class);
                i.putExtra("imageUrl",user.getImageUri());

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<CircleImageView,String>(binding.ivProfile,"imageProfile");

                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),pairs);
                startActivityForResult(i,MainActivity.REQUEST_RETURN_PROFILE,activityOptions.toBundle());
            }
        });

    }

    private void loadBadgeRecycler() {
        list = new ArrayList();
        list.add(new Badge(1,false));
        list.add(new Badge(2,false));
        list.add(new Badge(3,false));
        list.add(new Badge(4,false));
        list.add(new Badge(5,false));
        adapter = new BadgeAdapter(list,getActivity());
        binding.badgeRecycler.setAdapter(adapter);
    }

    private void loadData() {
        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);

                binding.edtvName.setText(user.getName());
                binding.edtvPhoneNo.setText(user.getPhoneNo());

                if (!user.getImageUri().equals("")){
                    if (URLUtil.isValidUrl(user.getImageUri())){
                        Glide.with(getActivity().getApplicationContext()).load(user.getImageUri())
                                .into(binding.ivProfile);
                    }
                }else {
                    if (Global.isDarkMode(getActivity())){
                        Glide.with(getActivity().getApplicationContext()).load(R.drawable.ic_baseline_account_circle_24_dark)
                                .into(binding.ivProfile);
                    }else {
                        Glide.with(getActivity().getApplicationContext()).load(R.drawable.ic_baseline_account_circle_24_light)
                                .into(binding.ivProfile);
                    }
                }

                if (snapshot.hasChild(Global.LEVEL_1)){
                    list.get(0).setAchieved(true);
                    adapter.notifyDataSetChanged();
                }

                if (snapshot.hasChild(Global.LEVEL_2)){
                    list.get(1).setAchieved(true);
                    adapter.notifyDataSetChanged();
                }

                if (snapshot.hasChild(Global.LEVEL_3)){
                    list.get(2).setAchieved(true);
                    adapter.notifyDataSetChanged();
                }

                if (snapshot.hasChild(Global.LEVEL_4)){
                    list.get(3).setAchieved(true);
                    adapter.notifyDataSetChanged();
                }

                if (snapshot.hasChild(Global.LEVEL_5)){

                    setUIasUserType(true);

                    list.get(4).setAchieved(true);
                    adapter.notifyDataSetChanged();
                }else {
                    setUIasUserType(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void setUIasUserType(boolean isPro){
        if (isPro){
            binding.proCrown.setVisibility(View.VISIBLE);
            binding.ivProfile.setBorderWidth(2);
            binding.normalTag.setVisibility(View.GONE);
            binding.proTag.setVisibility(View.VISIBLE);
        }else {
            binding.proCrown.setVisibility(View.GONE);
            binding.ivProfile.setBorderWidth(0);
            binding.normalTag.setVisibility(View.VISIBLE);
            binding.proTag.setVisibility(View.GONE);
        }
    }

    private boolean validateName(String name){
        if (name.equals("")){
            binding.edtvName.setError(getString(R.string.field_not_empty_msg));
            return false;
        }else if (name.length()>15){
            binding.edtvName.setError(getString(R.string.name_max_limit_msg));
            return false;
        }else if (name.length()<5){
            binding.edtvName.setError(getString(R.string.name_min_limit_msg));
            return false;
        }else {
            binding.edtvName.setError(null);
            return true;
        }
    }

    void setButtonNormal(){
        binding.btnId.revertAnimation();
        binding.btnId.setBackground(getActivity().getDrawable(R.drawable.button_bg));
    }

}