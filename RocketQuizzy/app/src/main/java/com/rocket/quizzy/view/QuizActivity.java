package com.rocket.quizzy.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.rocket.quizzy.adapter.OptionAdapter;
import com.rocket.quizzy.databinding.ActivityQuizBinding;
import com.rocket.quizzy.model.Option;
import com.rocket.quizzy.model.Question;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.custom.Alert_Dialog;
import com.rocket.quizzy.view.custom.Loading_Dialog;
import com.rocket.quizzy.view.custom.listeners.Alert_Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is my Quiz Activity where I had Put all the logic codes of helding quiz of any level.
 * Generally, I have posted only 5 levels as default. which are directly connected with Firebase.
 */

public class QuizActivity extends AppCompatActivity {

    // Below are my variables....
    ActivityQuizBinding binding;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    String level;
    String qID="";
    List<Question> questions;
    int counter = 0;
    int score = 0;
    int skippedQuestions = 0;
    MediaPlayer mediaPlayer;
    int viewedQuestion = 0;
    private MediaPlayer tmpMediaPlayer;
    public int selectedOption = -1;
    String selectedOptionAplpha;
    ArrayList<String> solUserSelOpt;
    CountDownTimer timer;
    private ImageView tmpBtnPlay;
    public int REQUEST_BADGE_ACHIEVEMENT = 1234;
    List<Option> options = new ArrayList<>();
    OptionAdapter optionAdapter;
    boolean customLevel;
    boolean isCameFromHomeFragment = false;
    boolean isCameFromFavFragment = false;

    ArrayList<String> userSelectedOptions = new ArrayList<>();
    Loading_Dialog loading_dialog;
    private boolean solutions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Hooks Starts from here */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);

        DayNight dayNight = new DayNight(this);

        dayNight.checkContentView(binding.contentView);
        dayNight.checkCardView(binding.layoutIvQuestion);
        dayNight.checkTextView(binding.tvQuestion,R.color.themeColor);
        dayNight.checkCardView(binding.layoutAudioQuestion);
        dayNight.checkCardView(binding.layoutTextQuestion);
        dayNight.checkContentView(binding.rl2);

        Intent intent = getIntent();
        if (intent.hasExtra("isCameFromHomeFragment")){
            isCameFromHomeFragment = intent.getBooleanExtra("isCameFromHomeFragment",false);
        }

        if (intent.hasExtra("fromFavFragment")){
            isCameFromFavFragment = intent.getBooleanExtra("fromFavFragment",false);
        }

        reference = FirebaseDatabase.getInstance().getReference(Global.QUIZ);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference.keepSynced(true);
        questions = new ArrayList<>();
        level = getIntent().getStringExtra("quizLevel");
        if (getIntent().hasExtra("qID")){
            customLevel = true;
            qID = getIntent().getStringExtra("qID");
        }else {
            customLevel = false;
        }
        /* Hooks End here */

        //Below is the checker of network that will usually check that is the user connected to internet or not.....
        Global.networkCheck(this);

        /* This is the intent extra for getting user selected options if the user is coming here from result page.In my app, It is ScoreActivity.java */
        if (getIntent().hasExtra("userSelectedOptions")) {
            solUserSelOpt = getIntent().getStringArrayListExtra("userSelectedOptions");
        }

        /* This is a function which is calling for changing the accent color of  status bar*/
        Global.statusbarAccentColor(this);

        /*This boolean is checking that is the user coming from solutions page or not. */
        if (!solutions) {
            /*Timer :- It activates a timer when user will start the quiz, I am  currently using the 10 minutes of time limit...*/
            timer = new CountDownTimer(600000, 1000) {
                @Override
                public void onTick(long l) {
                    int minutes = (int) (l / (60 * 1000));
                    int seconds = (int) ((l / 1000) % 60);
                    /*Timer text is updating for showing the user of the remaining time.*/
                    @SuppressLint("DefaultLocale") String strTimeLeft = getString(R.string.time_left_msg) + String.format("%d:%02d", minutes, seconds);
                    binding.tvCount.setText(strTimeLeft);

                    if (l / 1000 <= 10) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            binding.tvCount.setTextColor(getColor(R.color.themeColor));
                        }
                        blinkCountTextView();
                    }
                }

                @Override
                public void onFinish() {
                    /*This statement will be called when time`s up... */
                    endUptheTest();
                }
            };
            /* Below line is using to start timer. */
            timer.start();
        }

        /* Intializing the Loading Dialog */
        loading_dialog = new Loading_Dialog(this);

        /* I had created two types of loading dialog in LoadingDialog.class */
        loading_dialog.startLoadingDialog();

        DatabaseReference db;

        if (customLevel){
            db = FirebaseDatabase.getInstance().getReference(Global.QUIZ).child(qID).child(Global.QUESTIONS);
        }else {
            db = FirebaseDatabase.getInstance().getReference(Global.QUIZ).child(level);
        }

        /*Calling the Value Event Listener */
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Clearing the questions arraylist if carrying any object...
                questions.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    /* Getting Each Question Object from firebase and adding to my questions arraylist... */
                    Question question = ds.getValue(Question.class);
                    questions.add(question);

                }

                /* Setting up question by calling a single method in an ease... :) */
                setUpQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                /* Here I am Acting any action if connection to Firebase failed unfortunately */
                loading_dialog.dismissDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });


        /* Action for clicking on close button... */
        binding.btnClose.setOnClickListener(v -> {
            if (!solutions) {
                /*Creating a dialog for confirming of quitting the quiz*/
                Alert_Dialog alert_dialog = new Alert_Dialog(QuizActivity.this,
                        getString(R.string.quit_txt),
                        getString(R.string.quit_desc_txt));

                alert_dialog.showAlert(new Alert_Listener() {
                    @Override
                    public void onYesClick(Dialog dialog) {
                        timer.cancel();
                        dialog.dismiss();
                        if (isCameFromHomeFragment){
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra(Global.REQUEST_QUIT_KEY,Global.HOME_REQUEST);
                            startActivity(i);
                            Animatoo.animateZoom(QuizActivity.this);
                        }else if (isCameFromFavFragment){
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra(Global.REQUEST_QUIT_KEY,Global.FAV_REQUEST);
                            startActivity(i);
                            Animatoo.animateZoom(QuizActivity.this);
                        }
                        finish();
                    }

                    @Override
                    public void onNoClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            } else {
                finish();
            }
        });


    }

    /*Ending up the test by a method */
    void endUptheTest() {
        int unviewedQuestion = 0;
        if (viewedQuestion < questions.size()) {
            unviewedQuestion = questions.size() - viewedQuestion;

            int i = 0;
            do {
                i++;
                userSelectedOptions.add("-1");

            } while (i == unviewedQuestion);
        }
        timer.cancel();

        loading_dialog.dismissDialog();
        /* Transferring activity to Scoreboard when Quiz ends up */
        startActivityForResult(new Intent(getApplicationContext(), ScoreActivity.class)
                .putExtra("userSelectedOptions", userSelectedOptions)
                .putExtra("totalQuestions", questions.size())
                .putExtra("skippedQuestions", skippedQuestions)
                .putExtra("level", level)
                .putExtra("unviwedQuestions", unviewedQuestion)
                .putExtra("score", score), REQUEST_BADGE_ACHIEVEMENT);
    }



    ;

    // Setting up the question
    private void setUpQuestion() {
        if (questions.size() > 0) {
            options.clear();


            if (counter == questions.size() - 1) {
                viewedQuestion += 1;
            }

            Question question = questions.get(counter);
            String quesNo = String.valueOf(counter + 1);

            /* Changing the floating button icon when it`s the last question*/
            if (counter + 1 == questions.size()) {
                if (solutions) {
                    binding.nextBtn.setVisibility(View.GONE);
                } else {
                    binding.nextBtn.setImageResource(R.drawable.submit);
                }
            } else {
                binding.nextBtn.setImageResource(R.drawable.right_arrow);
                binding.nextBtn.setVisibility(View.VISIBLE);
            }

            /*Initializing the Question according to their types start from here----------------->*/
            binding.tvQuestionNo.setText(getString(R.string.question_txt) + quesNo + getString(R.string.of_txt) + questions.size());
            if (question.getQuestionType().equals(Question.QUESTION_TEXT)) {
                binding.layoutAudioQuestion.setVisibility(View.GONE);
                binding.layoutIvQuestion.setVisibility(View.GONE);
                binding.tvQuestion.setVisibility(View.VISIBLE);
                loading_dialog.dismissDialog();
                binding.nextBtn.setEnabled(true);
                binding.backBtn.setEnabled(true);
                binding.tvQuestion.setText(question.getQuestionText());
            } else if (question.getQuestionType().equals(Question.QUESTION_IMAGE)) {
                binding.layoutAudioQuestion.setVisibility(View.GONE);
                binding.layoutIvQuestion.setVisibility(View.VISIBLE);
                binding.tvQuestion.setVisibility(View.VISIBLE);

                binding.tvQuestion.setText(question.getQuestionText());

                Uri uri = null;


                uri = Uri.parse(question.getQuestionUrl());


                Glide.with(getApplicationContext()).load(uri).placeholder(R.color.grey)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                loading_dialog.dismissDialog();
                                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                loading_dialog.dismissDialog();
                                binding.nextBtn.setEnabled(true);
                                binding.backBtn.setEnabled(true);
                                binding.ivQuestion.setImageDrawable(resource);
                                return false;
                            }
                        }).into(binding.ivQuestion);
            } else if (question.getQuestionType().equals(Question.QUESTION_AUDIO)) {
                binding.layoutAudioQuestion.setVisibility(View.VISIBLE);
                binding.layoutIvQuestion.setVisibility(View.GONE);
                binding.tvQuestion.setVisibility(View.VISIBLE);

                binding.tvQuestion.setText(question.getQuestionText());

                if (tmpMediaPlayer != null) {
                    tmpMediaPlayer.stop();
                }
                mediaPlayer = new MediaPlayer();
                Uri uri = null;

                uri = Uri.parse(question.getQuestionUrl());


                if (getApplicationContext().checkCallingOrSelfPermission(Manifest.permission_group.MICROPHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    binding.waveVisualizer.setAudioSessionId(mediaPlayer.getAudioSessionId());
                }

                loading_dialog.dismissDialog();


                Uri finalUri = uri;
                binding.questionAudioPlayPtn.setOnClickListener(view -> {
                    loading_dialog.startLoadingDialog();
                    try {
                        if (finalUri != null) {
                            mediaPlayer.setDataSource(QuizActivity.this, finalUri);
                            mediaPlayer.prepare();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.setOnPreparedListener(mp -> {
                        loading_dialog.dismissDialog();
                        binding.nextBtn.setEnabled(true);
                        binding.backBtn.setEnabled(true);
                    });

                    if (tmpBtnPlay != null) {
                        tmpBtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                    }

                    binding.questionAudioPlayPtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));

                    mediaPlayer.start();

                    /*Handling the action when user clicks on the play button */
                    binding.questionAudioPlayPtn.setOnClickListener(view1 -> {
                        /* Checking the media Player if it is null or not */
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                binding.questionAudioPlayPtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                mediaPlayer.pause();
                            } else {
                                binding.questionAudioPlayPtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                                mediaPlayer.start();
                            }
                        }
                    });

                    /* Handling action when media ends or completes */
                    if (mediaPlayer != null) {
                        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> binding.questionAudioPlayPtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24)));

                    }

                    binding.waveVisualizer.setAudioSessionId(mediaPlayer.getAudioSessionId());

                    tmpMediaPlayer = mediaPlayer;

                    tmpBtnPlay = binding.questionAudioPlayPtn;
                });
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.sth_went_wrong), Toast.LENGTH_SHORT).show();
            }

            /*<--------------------------------Initializing the Question according to their types start from here*/




            /* Initializing the Options starts from here--------> */
            if (question.getOptionType().equals(Option.OPTION_TEXT)) {
                for (int i = 0; i < question.getOptionsText().size(); i++) {
                    options.add(new Option(i + 1, question.getOptionsText().get(i), question.getOptionType(), ""));
                }
            } else if (question.getOptionType().equals(Option.OPTION_IMAGE) || question.getOptionType().equals(Option.OPTION_AUDIO)) {
                if (question.getOptionsText().size() == question.getOptionsUrl().size()) {
                    for (int i = 0; i < question.getOptionsText().size(); i++) {
                        options.add(new Option(i + 1, question.getOptionsText().get(i), question.getOptionType(), question.getOptionsUrl().get(i)));
                    }
                } else {
                    options.add(new Option(1, question.getOptionsText().get(0), question.getOptionType(), question.getOptionsUrl().get(0)));
                    options.add(new Option(2, question.getOptionsText().get(1), question.getOptionType(), question.getOptionsUrl().get(1)));
                    options.add(new Option(3, question.getOptionsText().get(2), question.getOptionType(), question.getOptionsUrl().get(2)));
                    options.add(new Option(4, question.getOptionsText().get(3), question.getOptionType(), question.getOptionsUrl().get(3)));
                }
            }

            /* ------------->Initializing the Options ends here */

            // Checking and setting the solutions boolean.....
            if (getIntent().hasExtra("quiz.score.solutions")) {

                boolean isSolutions = getIntent().getBooleanExtra("quiz.score.solutions", false);
                if (isSolutions) {
                    solutions = true;
                }

            }

            /* Visualizing the back button if user is coming from solutions  */
            if (solutions) {
                if (counter - 1 == -1) {
                    binding.backBtn.setVisibility(View.GONE);
                } else {
                    binding.backBtn.setVisibility(View.VISIBLE);
                }

                binding.tvCount.setVisibility(View.GONE);
            } else {
                binding.tvCount.setVisibility(View.VISIBLE);
            }

            /* Setting up the options adapter to recyclerView */
            optionAdapter = new OptionAdapter(options, level, counter, getApplicationContext(), QuizActivity.this, solutions);
            binding.optionRecycler.setAdapter(optionAdapter);


            /* Handling the Actions when user click the next button */
            binding.nextBtn.setOnClickListener(view -> {

                if (!solutions) {
                    viewedQuestion += 1;
                    if (selectedOption != -1) {
                        scoreMarks(question);
                    } else {
                        userSelectedOptions.add("-1");
                        skippedQuestions += 1;
                    }
                }

                if (tmpMediaPlayer != null) {
                    tmpMediaPlayer.stop();
                    binding.questionAudioPlayPtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
                loading_dialog.startLoadingDialog();

                if (counter == questions.size() - 1) {
                    if (!solutions) {
                        Alert_Dialog alert_dialog = new Alert_Dialog(QuizActivity.this,
                                getString(R.string.end_up),
                                getString(R.string.quiz_end_desc_msg));
                        alert_dialog.showAlert(new Alert_Listener() {
                            @Override
                            public void onYesClick(Dialog dialog) {
                                dialog.dismiss();
                                endUptheTest();
                            }

                            @Override
                            public void onNoClick(Dialog dialog) {
                                dialog.dismiss();
                                loading_dialog.dismissDialog();
                            }
                        });
                    }
                } else {
                    next();
                }
            });

            /*Setting the actions when user click back button*/
            binding.backBtn.setOnClickListener(v -> {
                if (tmpMediaPlayer != null) {
                    tmpMediaPlayer.stop();
                    binding.questionAudioPlayPtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
                loading_dialog.startLoadingDialog();
                binding.backBtn.setEnabled(false);
                counter--;
                setUpQuestion();
            });

        }
    }

    @Override
    public void onBackPressed() {

        /* Handling the actions when the user presses the back button */
        if (!solutions) {
            Alert_Dialog alert_dialog = new Alert_Dialog(QuizActivity.this,
                    getString(R.string.quit),
                    getString(R.string.quiz_quit_dialog_desc_msg));
            alert_dialog.showAlert(new Alert_Listener() {
                @Override
                public void onYesClick(Dialog dialog) {
                    timer.cancel();
                    dialog.dismiss();

                    if (isCameFromHomeFragment){
                        Intent i = new Intent();
                        i.putExtra(Global.REQUEST_QUIT_KEY,Global.HOME_REQUEST);
                        setResult(RESULT_OK,i);
                    }else if (isCameFromFavFragment){
                        Intent i = new Intent();
                        i.putExtra(Global.REQUEST_QUIT_KEY,Global.FAV_REQUEST);
                        setResult(RESULT_OK,i);
                    }

                    finish();
                }

                @Override
                public void onNoClick(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    /* Method for adding the marks */
    private void scoreMarks(Question question) {

        if (question.getOptionType().equals(Option.OPTION_TEXT)) {
            userSelectedOptions.add(question.getOptionsText().get(selectedOption));
            if (question.getOptionsText().get(selectedOption).equals(question.getCorrectAnswer())) {
                score += 10;
                selectedOption = -1;
            }
        } else if (question.getOptionType().equals(Option.OPTION_IMAGE) ||
                question.getOptionType().equals(Option.OPTION_AUDIO)) {
            userSelectedOptions.add(question.getOptionsUrl().get(selectedOption));
            if (question.getOptionsUrl().get(selectedOption).equals(question.getCorrectAnswer())) {
                score += 10;
                selectedOption = -1;
            }
        }

    }


    /* Handling method for next click */
    private void next() {
        counter++;
        selectedOption = -1;
        setUpQuestion();
    }

    /* This method will usually changes the activity visually as well as logically when user selects any option */
    public void selectCurrentOption(CardView main_option_ll, int position, TextView tvSerialNo, TextView tvOption, String optionAlphaNo) {
        if (optionAdapter != null) {
            selectedOption = position;

            optionAdapter.notifyDataSetChanged();

            main_option_ll.setCardBackgroundColor(getResources().getColor(R.color.themeColor));
            tvOption.setTextColor(getResources().getColor(R.color.white));
            tvSerialNo.setTextColor(getResources().getColor(R.color.white));

        }
    }

    /* Method for blinking the text when last 10 seconds are left */
    private void blinkCountTextView() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.tvCount.getVisibility() == View.VISIBLE) {
                            binding.tvCount.setVisibility(View.INVISIBLE);
                        } else {
                            binding.tvCount.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    /* This method is calling up from options adapter.. This is for solutions only.. */
    public void setOptionUI(int position, CardView itemView, TextView tvOption) {

        if (questions.size() == solUserSelOpt.size()) {
            Question question = questions.get(counter);

            if (options.get(position).getOptionType().equals(Option.OPTION_TEXT)) {
                if (options.get(position).getOptionText().equals(question.getCorrectAnswer())) {
                    itemView.setCardBackgroundColor(getResources().getColor(R.color.green));
                    tvOption.setTextColor(getResources().getColor(R.color.white));
                } else {
                    if (options.get(position).getOptionText().equals(solUserSelOpt.get(counter))) {
                        itemView.setCardBackgroundColor(getResources().getColor(R.color.red));
                        tvOption.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            } else {
                if (options.get(position).getOptionUrl().equals(question.getCorrectAnswer())) {
                    itemView.setCardBackgroundColor(getResources().getColor(R.color.green));
                    tvOption.setTextColor(getResources().getColor(R.color.white));
                } else {
                    if (options.get(position).getOptionUrl().equals(solUserSelOpt.get(counter))) {
                        itemView.setCardBackgroundColor(getResources().getColor(R.color.red));
                        tvOption.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }


        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.sth_went_wrong), Toast.LENGTH_SHORT).show();
        }

    }

    /* Performing actions when this activity will get any request.. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BADGE_ACHIEVEMENT
                && resultCode == RESULT_OK) {
            boolean isBadgeAchieved = data.getBooleanExtra("isBadgeAchieved", false);
            boolean isFirstTime = data.getBooleanExtra("isFirstTime", false);
            String qlevel = data.getStringExtra("level");

            Intent intent = new Intent();
            intent.putExtra("isBadgeAchieved", isBadgeAchieved);
            intent.putExtra("isFirstTime", isFirstTime);
            intent.putExtra("level", qlevel);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }
}