package com.rocket.quizzy.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.chinalwb.slidetoconfirmlib.ISlideListener;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.MainActivity;
import com.rocket.quizzy.R;
import com.rocket.quizzy.adapter.AddOptionAdapter;
import com.rocket.quizzy.adapter.AddQuestionAdapter;
import com.rocket.quizzy.databinding.ActivityAddQuizBinding;
import com.rocket.quizzy.model.AddOption;
import com.rocket.quizzy.model.Question;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.service.FirebaseService;
import com.rocket.quizzy.service.interfaces.FireResult;
import com.rocket.quizzy.view.custom.DialogInterface;
import com.rocket.quizzy.view.custom.Loading_Dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddQuizActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int BANNER_REQUEST = 1;
    private static final int DIALOG_IMAGE_REQUEST = 2;
    private static final int DIALOG_AUDIO_REQUEST = 3;
    private static final int CASE_TEXT = 5;
    private static final int CASE_IMAGE = 6;
    private static final int CASE_AUDIO = 7;
    public int selectedOption = -1;
    ActivityAddQuizBinding binding;
    FirebaseService firebaseService;
    Uri bannerUri;
    Uri questionImageUri;
    Uri questionAudioUri;
    String quizTitle = "";
    String quizDesc = "";
    String bannerUrl = "";
    String newQuestionUrl = "";
    private MediaPlayer tmpMediaPlayer;
    MediaPlayer mediaPlayer;
    String questionType = "";
    Dialog questionDialog;
    private ImageView tmpBtnPlay;
    ImageView questionImage;
    ImageView audioPlayBtn;
    DatabaseReference reference;
    String newQuestionKey;
    CardView imageLayout;
    LinearLayout audioLayout;
    TextView tvImageUpload;
    TextView tvAudioUpload;
    List<Question> questions = new ArrayList<>();
    AddQuestionAdapter addQuestionAdapter;
    List<AddOption> dialogOptionList = new ArrayList<AddOption>();
    private WaveVisualizer waveVisualizer;
    AddOptionAdapter addOptionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_quiz);
        firebaseService = new FirebaseService(this);
        reference = FirebaseDatabase.getInstance().getReference();

        DayNight dayNight = new DayNight(this);

        dayNight.checkContentView(binding.contentView);
        dayNight.checkToolbar(binding.toolbar);
        dayNight.checkImageView(binding.ivR);
        dayNight.checkImageView(binding.backBtn);
        dayNight.checkCardView(binding.edtvQuizTitleLayout);
        dayNight.checkCardView(binding.edtvQuizDescLayout);
        dayNight.checkCardView(binding.ivThumbnailLayout);
        dayNight.checkEditText(binding.edtvQuizDesc);
        dayNight.checkEditText(binding.edtvQuizTitle);
        dayNight.checkTextView(binding.uploadTv,R.color.themeColor);
        dayNight.checkTextView(binding.tvTitle,R.color.grey);
        dayNight.checkTextView(binding.tvTitle2,R.color.grey);
        dayNight.checkTextView(binding.descCharCount,R.color.themeColor);
        dayNight.checkTextView(binding.tvToolbarTitle,R.color.themeColor);


        binding.part1Info.setVisibility(View.VISIBLE);
        binding.part2Info.setVisibility(View.GONE);
        
        Global.networkCheck(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        part1info();
        part2info();

    }

    private void part2info() {
//        if (!quizTitle.isEmpty() | !quizDesc.isEmpty() | !bannerUrl.isEmpty()) {
//
//
//        } else {
//            binding.part1Info.setVisibility(View.VISIBLE);
//            binding.part2Info.setVisibility(View.GONE);
//        }

        addQuestionAdapter = new AddQuestionAdapter(questions,getApplicationContext());
        binding.recyclerQuestions.setAdapter(addQuestionAdapter);

        binding.addQuestionBtn.setOnClickListener(v -> {
            newQuestionKey = reference.push().getKey();
            part2info();
            if (questionDialog!=null){
                questionDialog.show();
            }
        });

        dialogOptionList.clear();

        questionDialog = new Dialog(AddQuizActivity.this);
        ColorDrawable colorDrawable = new ColorDrawable(R.drawable.transparent_corner);
        InsetDrawable insetDrawable = new InsetDrawable(colorDrawable, 20);

        questionDialog.getWindow().setBackgroundDrawable(insetDrawable);
        questionDialog.setContentView(R.layout.dialog_add_question);
        questionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionDialog.setCancelable(false);
        questionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Spinner spinner = questionDialog.findViewById(R.id.question_type);
//            LinearLayout mainLayout = questionDialog.findViewById(R.id.main_layout);
//            LinearLayout textQuestionLayout = questionDialog.findViewById(R.id.layout_text_question);
        imageLayout = questionDialog.findViewById(R.id.layout_image_question);
        CardView mainAudioLayout = questionDialog.findViewById(R.id.layout_audio_question);
        CardView selectQTypeLayout = questionDialog.findViewById(R.id.select_q_type_layout);
        CardView layoutTextQuestion = questionDialog.findViewById(R.id.layout_text_question);
        questionImage = questionDialog.findViewById(R.id.iv_question);
        audioPlayBtn = questionDialog.findViewById(R.id.question_audio_play_btn);
        tvImageUpload = questionDialog.findViewById(R.id.upload_tv);
        tvAudioUpload = questionDialog.findViewById(R.id.upload_tv_audio);
        waveVisualizer = questionDialog.findViewById(R.id.wave_visualizer);
        audioLayout = questionDialog.findViewById(R.id.layout_audio_preview);
        FloatingActionButton addOptionBtn = questionDialog.findViewById(R.id.optionAddBtn);
        RecyclerView optionRecyclerView = questionDialog.findViewById(R.id.add_option_list);
        TextView cancel = questionDialog.findViewById(R.id.cancel_btn);
        Button yes = questionDialog.findViewById(R.id.yes_btn);
        EditText edtvQuestion = questionDialog.findViewById(R.id.edtv_question);
        LinearLayout contentView = questionDialog.findViewById(R.id.linear_layout);
        TextView tv_mark_the_c_ans = questionDialog.findViewById(R.id.tv_mark_the_c_ans);
        TextView tv_dialog_title = questionDialog.findViewById(R.id.tv_dialog_title);

        DayNight dayNight = new DayNight(AddQuizActivity.this);
        dayNight.checkDialogContentView(contentView);
        dayNight.checkCardView(selectQTypeLayout);
        dayNight.checkCardView(imageLayout);
        dayNight.checkCardView(mainAudioLayout);
        dayNight.checkCardView(layoutTextQuestion);
        dayNight.checkEditText(edtvQuestion);
        dayNight.checkTextView(tv_mark_the_c_ans,R.color.grey);
        dayNight.checkTextView(tv_dialog_title,R.color.themeColor);
        dayNight.checkTextView(cancel,R.color.grey,R.color.red);

        binding.backBtn.setOnClickListener(v -> {

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra(Global.REQUEST_QUIT_KEY,Global.MAGIC_PLAY_REQUEST);
            startActivity(i);
            Animatoo.animateZoom(AddQuizActivity.this);
            finish();

        });

        if (tmpMediaPlayer != null) {
            tmpMediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();

        if (getApplicationContext().checkCallingOrSelfPermission(Manifest.permission_group.MICROPHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            waveVisualizer.setAudioSessionId(mediaPlayer.getAudioSessionId());
        }

        //Two Options are default and compulsary to be added in the list...
        AddOption option1 = new AddOption(1, "");
        AddOption option2 = new AddOption(2, "");

        addOptionAdapter = new AddOptionAdapter(dialogOptionList, getApplicationContext(), AddQuizActivity.this);

        if (dialogOptionList.size() >= 5) {
            addOptionBtn.setVisibility(View.GONE);
        } else {
            addOptionBtn.setVisibility(View.VISIBLE);
        }

        optionRecyclerView.setAdapter(addOptionAdapter);
        dialogOptionList.add(option1);
        dialogOptionList.add(option2);
        addOptionAdapter.notifyDataSetChanged();

        addOptionBtn.setOnClickListener(view -> {
            if (dialogOptionList.size() >= 5) {
                Toast.makeText(this, R.string.option_restriction_msg, Toast.LENGTH_SHORT).show();
            } else {
                dialogOptionList.add(new AddOption(dialogOptionList.size() + 1, ""));
                addOptionAdapter.notifyDataSetChanged();
            }
        });

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.question_types)){
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
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        imageLayout.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent,getString(R.string.select_question_img_msg)), DIALOG_IMAGE_REQUEST);
        });
        mainAudioLayout.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,getString(R.string.select_question_audio_msg)), DIALOG_AUDIO_REQUEST);
        });

        Loading_Dialog loading_dialog = new Loading_Dialog(AddQuizActivity.this);

        cancel.setOnClickListener(v -> {
            clearDialogData();
            questionDialog.dismiss();
        });

        yes.setOnClickListener(view -> {
            if (!questionType.equals("")) {
                if (questionType.equals(Question.QUESTION_IMAGE)) {
                    if (questionImageUri == null){
                        Toast.makeText(this, getString(R.string.image_not_empty_msg), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (questionType.equals(Question.QUESTION_AUDIO)) {
                    if (questionAudioUri == null){
                        Toast.makeText(this, getString(R.string.audio_not_empty_msg), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (questionType.equals("TEXT")) {
                    loading_dialog.startLoadingDialog();
                } else {
                    loading_dialog.startLoadingDialog(new DialogInterface() {
                        @Override
                        public void onLoadingProgressBar(Dialog dialog) {
                            RoundCornerProgressBar progressBar = dialog.findViewById(R.id.hori_progress_Bar);
                            TextView percent = dialog.findViewById(R.id.tv_percent);

                            firebaseService.uploadImagetoFirebaseStorage((questionType.equals("IMAGE")) ? questionImageUri : questionAudioUri, progressBar, percent,
                                    new FireResult() {
                                        @Override
                                        public void onUploadSuccess(String mediaUrl) {
                                            newQuestionUrl = mediaUrl;
                                        }

                                        @Override
                                        public void onUploadFailure(String error) {
                                            loading_dialog.dismissDialog();
                                            Toast.makeText(AddQuizActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
                if (!edtvQuestion.getText().toString().isEmpty()) {
                    boolean AreAllOptionsFilled = true;
                    for (AddOption addOption : dialogOptionList) {
                        if (addOption.getOptionName().isEmpty()) {
                            AreAllOptionsFilled = false;
                        }
                    }
                    if (AreAllOptionsFilled) {
                        if (newQuestionKey.isEmpty()) {
                            newQuestionKey = reference.push().getKey();
                        }


                        if (selectedOption == -1){
                            loading_dialog.dismissDialog();
                            Toast.makeText(this, getString(R.string.mark_the_c_ans_msg), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ArrayList<String> optionList = new ArrayList<String>();
                        optionList.clear();
                        for (AddOption addOption : dialogOptionList) {
                            optionList.add(addOption.getOptionName());
                        }
                        Question question = new Question(newQuestionKey,
                                Global.PUBLIC_LEVEL_KEY,
                                questionType,
                                edtvQuestion.getText().toString(),
                                newQuestionUrl,
                                "TEXT",
                                dialogOptionList.get(selectedOption).getOptionName(),
                                null,
                                optionList);

                        questions.add(question);
                        if (addQuestionAdapter!=null){
                            addQuestionAdapter.notifyDataSetChanged();
                        }
                        clearDialogData();
                        questionDialog.dismiss();
                        loading_dialog.dismissDialog();
                    } else {
                        loading_dialog.dismissDialog();
                        Toast.makeText(this, getString(R.string.neither_option_empty_msg), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loading_dialog.dismissDialog();
                    Toast.makeText(this, getString(R.string.question_empty_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinner.setOnItemSelectedListener(AddQuizActivity.this);


        if (audioPlayBtn!=null){
            audioPlayBtn.setOnClickListener(view -> {
                if (questionAudioUri != null) {
                    try {
                        if (questionAudioUri != null) {
                            mediaPlayer.setDataSource(AddQuizActivity.this, questionAudioUri);
                            mediaPlayer.prepare();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (tmpBtnPlay != null) {
                        tmpBtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                    }

                    audioPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));

                    mediaPlayer.start();

                    /*Handling the action when user clicks on the play button */
                    audioPlayBtn.setOnClickListener(view1 -> {
                        /* Checking the media Player if it is null or not */
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                audioPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                mediaPlayer.pause();
                            } else {
                                audioPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                                mediaPlayer.start();
                            }
                        }
                    });

                    /* Handling action when media ends or completes */
                    if (mediaPlayer != null) {
                        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> audioPlayBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24)));

                    }

                    if (getApplicationContext().checkCallingOrSelfPermission(Manifest.permission_group.MICROPHONE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        waveVisualizer.setAudioSessionId(mediaPlayer.getAudioSessionId());
                    }

                    tmpMediaPlayer = mediaPlayer;

                    tmpBtnPlay = audioPlayBtn;
                } else {
                    Toast.makeText(this, getString(R.string.sth_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });
        }

        binding.slideToConfirm.setSlideListener(new ISlideListener() {
            @Override
            public void onSlideStart() {

            }

            @Override
            public void onSlideMove(float percent) {

            }

            @Override
            public void onSlideCancel() {

            }

            @Override
            public void onSlideDone() {
                Toast.makeText(AddQuizActivity.this, getString(R.string.quiz_review_msg), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearDialogData() {
        dialogOptionList.clear();
        selectedOption = -1;
        newQuestionKey = "";
        newQuestionUrl = "";
        questionImageUri = null;
        questionAudioUri = null;
        addOptionAdapter = null;
        questionType = "";
        tmpMediaPlayer = null;
        mediaPlayer = null;

    }

    private void part1info() {

        binding.edtvQuizDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.descCharCount.setText(binding.edtvQuizDesc.getText().toString().length() + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Loading_Dialog loading = new Loading_Dialog(AddQuizActivity.this);

        binding.nextBtn.setOnClickListener(v -> {

            if (validateEDTV(binding.edtvQuizTitle, "Title") | validateEDTV(binding.edtvQuizDesc, "Description")) {
                if (bannerUri != null) {
                    loading.startLoadingDialog(new DialogInterface() {
                        @Override
                        public void onLoadingProgressBar(Dialog dialog) {
                            RoundCornerProgressBar progressBar = dialog.findViewById(R.id.hori_progress_Bar);
                            TextView percent = dialog.findViewById(R.id.tv_percent);
                            firebaseService.uploadImagetoFirebaseStorage(bannerUri, progressBar, percent, new FireResult() {
                                @Override
                                public void onUploadSuccess(String imageUrl) {
                                    bannerUrl = imageUrl;
                                    binding.part1Info.setVisibility(View.GONE);
                                    binding.part2Info.setVisibility(View.VISIBLE);
                                    loading.dismissDialog();
                                }

                                @Override
                                public void onUploadFailure(String error) {
                                    loading.dismissDialog();
                                    Toast.makeText(AddQuizActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(this, getString(R.string.banner_not_empty_msg), Toast.LENGTH_SHORT).show();
                }
            }

        });
        binding.layoutBanner.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, BANNER_REQUEST);

        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), AddQuizActivity.class);
        i.putExtra(Global.REQUEST_QUIT_KEY,Global.MAGIC_PLAY_REQUEST);
        startActivity(i);
        Animatoo.animateZoom(AddQuizActivity.this);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == BANNER_REQUEST) {

                if (data.getData() != null) {
                    bannerUri = data.getData();
                    binding.uploadTv.setVisibility(View.GONE);
                    binding.ivBanner.setVisibility(View.VISIBLE);
                    binding.ivBanner.setImageURI(data.getData());
                }

            } else if (requestCode == DIALOG_IMAGE_REQUEST) {
                if (data.getData() != null) {
                    questionImageUri = data.getData();
                    tvImageUpload.setVisibility(View.GONE);
                    questionImage.setVisibility(View.VISIBLE);
                    questionImage.setImageURI(data.getData());
                }
            } else if (requestCode == DIALOG_AUDIO_REQUEST) {
                if (data.getData() != null) {
                    questionAudioUri = data.getData();
                    tvAudioUpload.setVisibility(View.GONE);
                    audioLayout.setVisibility(View.VISIBLE);
                }
            }

        }

    }


    /*
Validation Functions
 */

    private boolean validateEDTV(EditText edtv, String fieldName) {
        String title = edtv.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, fieldName + getString(R.string.cannot_empty_msg), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void updateDialogView(int Case) {
        if (questionDialog != null) {
            LinearLayout mainLayout = questionDialog.findViewById(R.id.main_layout);
            CardView textQuestionLayout = questionDialog.findViewById(R.id.layout_text_question);
            CardView imageQuestionLayout = questionDialog.findViewById(R.id.layout_image_question);
            CardView audioQuestionLayout = questionDialog.findViewById(R.id.layout_audio_question);
            ImageView ivQuestion = questionDialog.findViewById(R.id.iv_question);
            ImageView questionAudioPlayBtn = questionDialog.findViewById(R.id.question_audio_play_btn);
            TextView tvImageUpload = questionDialog.findViewById(R.id.upload_tv);
            TextView uploadTvAudio = questionDialog.findViewById(R.id.upload_tv_audio);
            WaveVisualizer waveVisualizer = questionDialog.findViewById(R.id.wave_visualizer);
            LinearLayout layoutAudioPreview = questionDialog.findViewById(R.id.layout_audio_preview);
            switch (Case) {
                case CASE_TEXT:
                    textQuestionLayout.setVisibility(View.VISIBLE);
                    imageQuestionLayout.setVisibility(View.GONE);
                    audioQuestionLayout.setVisibility(View.GONE);
                    questionType = "TEXT";
                    break;
                case CASE_IMAGE:
                    textQuestionLayout.setVisibility(View.VISIBLE);
                    imageQuestionLayout.setVisibility(View.VISIBLE);
                    audioQuestionLayout.setVisibility(View.GONE);
                    ivQuestion.setVisibility(View.GONE);
                    tvImageUpload.setVisibility(View.VISIBLE);
                    questionType = "IMAGE";
                    break;
                case CASE_AUDIO:
                    textQuestionLayout.setVisibility(View.VISIBLE);
                    imageQuestionLayout.setVisibility(View.GONE);
                    audioQuestionLayout.setVisibility(View.VISIBLE);
                    layoutAudioPreview.setVisibility(View.GONE);
                    uploadTvAudio.setVisibility(View.VISIBLE);
                    questionType = "AUDIO";
                    break;

            }
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    public void selectOption(int position) {
        selectedOption = position;
        if (addOptionAdapter!=null){
            binding.recyclerQuestions.post(new Runnable() {
                @Override
                public void run() {
                    addOptionAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {

            case 0:
                break;
            case 1:
                updateDialogView(CASE_TEXT);
                break;
            case 2:
                updateDialogView(CASE_IMAGE);
                break;
            case 3:
                updateDialogView(CASE_AUDIO);
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}