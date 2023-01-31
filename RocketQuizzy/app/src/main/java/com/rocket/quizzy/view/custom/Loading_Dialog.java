package com.rocket.quizzy.view.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.custom.DialogInterface;

import com.rocket.quizzy.R;

public class Loading_Dialog {

    private Activity activity;
    private AlertDialog dialog;

    public Loading_Dialog(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoadingDialog(DialogInterface dialogInterface) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();

        dialog.show();

        CardView cardView = dialog.findViewById(R.id.cardView);

        DayNight dayNight = new DayNight(activity);
        dayNight.checkCardView(cardView);

        dialogInterface.onLoadingProgressBar(dialog);
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}

