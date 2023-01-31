package com.rocket.quizzy.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;

import java.io.IOException;

public class AudioService {
    private Context context;
    private MediaPlayer tmpMediaPlayer;

    public AudioService(Context context) {
        this.context = context;

    }

    public void playAudioFromUrl(String url, final OnPlayCallBack onPlayCallBack) {
        if (tmpMediaPlayer != null) {
            tmpMediaPlayer.stop();
        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            tmpMediaPlayer = mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                onPlayCallBack.onPrepared(mp);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onPlayCallBack.onFinished();
            }
        });
    }

    public interface OnPlayCallBack {
        void onPrepared(MediaPlayer mediaPlayer);

        void onFinished();
    }
}


