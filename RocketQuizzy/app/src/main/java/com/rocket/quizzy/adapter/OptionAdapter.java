package com.rocket.quizzy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rocket.quizzy.Global;
import com.rocket.quizzy.R;
import com.rocket.quizzy.model.Option;
import com.rocket.quizzy.service.AudioService;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.QuizActivity;
import com.rocket.quizzy.view.QuizInfoActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.Holder> {
    List<Option> list;
    Context context;
    private ImageView tmpBtnPlay;
    private MediaPlayer tmpMediaPlayer;
    String optionAlphaNo;
    QuizActivity activity;
    boolean solutions;
    String level;
    int counter;

    public OptionAdapter(List<Option> list,String level,int counter, Context context, QuizActivity activity, boolean solutions) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.solutions = solutions;
        this.level = level;
        this.counter = counter;
    }


    @NonNull
    @Override
    public OptionAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_option, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionAdapter.Holder holder, @SuppressLint("RecyclerView") int position) {

        Option option = list.get(position);

        DayNight dayNight = new DayNight(context);
        dayNight.checkContentView(holder.itemView);
        dayNight.checkCardView(holder.cardView);

        if (activity.selectedOption==position){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.themeColor));
            holder.tvOption.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvSerialNo.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor((Global.isDarkMode(context) ? R.color.darkLight : R.color.white)));
            holder.tvOption.setTextColor(context.getResources().getColor(Global.isDarkMode(context) ? R.color.white : R.color.themeColor));
            holder.tvSerialNo.setTextColor(context.getResources().getColor(Global.isDarkMode(context) ? R.color.white : R.color.themeColor));
        }

        if (option.getId()==1){
                optionAlphaNo = "A";
            }else if (option.getId()==2){
                    optionAlphaNo = "B";
            }else if (option.getId()==3){
                optionAlphaNo = "C";
            }else if (option.getId()==4){
                optionAlphaNo = "D";
            }

            holder.tvSerialNo.setText(optionAlphaNo+".");

        if (option.getOptionType().equals(Option.OPTION_TEXT)) {
            holder.imageOptionLayout.setVisibility(View.GONE);
            holder.audioLayout.setVisibility(View.GONE);
            holder.ivOption.setVisibility(View.GONE);
            holder.tvOption.setVisibility(View.VISIBLE);
            holder.tvOption.setText(option.getOptionText());
        } else if (option.getOptionType().equals(Option.OPTION_IMAGE)) {
            holder.imageOptionLayout.setVisibility(View.VISIBLE);
            holder.audioLayout.setVisibility(View.GONE);
            holder.ivOption.setVisibility(View.VISIBLE);
            holder.tvOption.setVisibility(View.GONE);

            Uri uri = null;


            uri = Uri.parse(option.getOptionUrl());

            Glide.with(context).load(option.getOptionUrl()).placeholder(R.color.grey).into(holder.ivOption);

        }else if (option.getOptionType().equals(Option.OPTION_AUDIO)) {
            holder.imageOptionLayout.setVisibility(View.GONE);
            holder.audioLayout.setVisibility(View.VISIBLE);
            holder.ivOption.setVisibility(View.GONE);
            holder.tvOption.setVisibility(View.GONE);
            if (tmpMediaPlayer != null) {
                tmpMediaPlayer.stop();
            }
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(option.getOptionUrl());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.optionAudioPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tmpBtnPlay != null) {
                        tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                    }

                    holder.optionAudioPlayButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_24));

                    mediaPlayer.start();

                    tmpMediaPlayer = mediaPlayer;

                    holder.optionAudioPlayButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mediaPlayer.isPlaying()) {
                                holder.optionAudioPlayButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                                mediaPlayer.pause();
                            } else {
                                holder.optionAudioPlayButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                                mediaPlayer.start();
                            }
                        }
                    });

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            holder.optionAudioPlayButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                        }
                    });


                    tmpBtnPlay = holder.optionAudioPlayButton;
                }
            });

        }

        if (solutions){
            activity.setOptionUI(position,holder.cardView,holder.tvOption);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (!solutions){
                activity.selectCurrentOption(holder.cardView,position,holder.tvSerialNo,holder.tvOption,optionAlphaNo);
            }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView tvSerialNo, tvOption;
        ImageView ivOption;
        WaveVisualizer waveVisualizer;
        ImageView optionAudioPlayButton;
        LinearLayout audioLayout;
        CardView imageOptionLayout, cardView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvSerialNo = itemView.findViewById(R.id.option_serial_no);
            tvOption = itemView.findViewById(R.id.tv_option);
            ivOption = itemView.findViewById(R.id.iv_option);
            waveVisualizer = itemView.findViewById(R.id.wave_visualizer);
            audioLayout = itemView.findViewById(R.id.layout_audio);
            cardView = itemView.findViewById(R.id.cardView);
            imageOptionLayout = itemView.findViewById(R.id.image_layout);
            optionAudioPlayButton = itemView.findViewById(R.id.option_audio_play_btn);
        }
    }
}
