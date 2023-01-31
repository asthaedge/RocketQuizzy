package com.rocket.quizzy.view.custom;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rocket.quizzy.R;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.custom.listeners.Alert_Listener;


public class Alert_Dialog {

    Activity activity;
    String title;
    String desc;


    /*
    This is the constructor of this Alert_Dialog class which will receive the required
    info like title and desc as well as the acitvity in which it had been used up.......
     */
    public Alert_Dialog(Activity activity, String title, String desc) {
        this.activity = activity;
        this.title = title;
        this.desc = desc;
    }

    //This method will show the Custom Alert.....
    public void showAlert(Alert_Listener listener){
        Dialog dialog = new Dialog(activity);
        ColorDrawable colorDrawable = new ColorDrawable(R.drawable.transparent_corner);
        InsetDrawable insetDrawable = new InsetDrawable(colorDrawable,20);

        dialog.getWindow().setBackgroundDrawable(insetDrawable);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvtitle = dialog.findViewById(R.id.tv_dialog_title);
        TextView tvdesc = dialog.findViewById(R.id.desc);
        TextView cancel = dialog.findViewById(R.id.cancel_btn);
        Button yes = dialog.findViewById(R.id.yes_btn);
        LinearLayout linearLayout = dialog.findViewById(R.id.linear_layout);

        DayNight dayNight = new DayNight(activity);
        dayNight.checkDialogContentView(linearLayout);
        dayNight.checkTextView(tvtitle,R.color.themeColor);
        dayNight.checkTextView(tvdesc,R.color.grey);
        dayNight.checkTextView(cancel,R.color.grey,R.color.red);

        //Here title of dialog is setting up
        tvtitle.setText(title);
        //Here desc of dialog is setting up
        tvdesc.setText(desc);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Here Listener will listen the command by the Activity if yes will be clicked up.....
                listener.onYesClick(dialog);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Here Dialog will simply Dismiss.....
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
