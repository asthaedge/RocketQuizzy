package com.rocket.quizzy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Objects;


public class Global {

    public static final int PUBLIC_LEVEL_KEY = 567;
    public static final String CATEGORIES = "CATEGORIES";
    public static final String FAVOURITE = "FAVOURITE";
    public static final String FAV_ID = "FAV_ID";
    public static final String QUESTIONS = "questions";
    public static final String BANNER_URL = "BANNER__URL";
    public static final String MISC = "MISC";
    public static final String BANNER = "BANNER";
    public static final String REQUEST_QUIT_KEY = "com.rocket.quizzy.quit.key";
    private static final String DARK_MODE = "DARK_MODE";
    private static final String LANGUAGE = "LANGUAGE";
    private static final String RECEIVE_NOTIFICATION = "RECEIVE_NOTIFICATION";
    public static String USERS = "USERS";
    public static String QUIZ = "QUIZ";

    public static String LEVEL_1 = "LEVEL_1";
    public static String LEVEL_2 = "LEVEL_2";
    public static String LEVEL_3 = "LEVEL_3";
    public static String LEVEL_4 = "LEVEL_4";
    public static String LEVEL_5 = "LEVEL_5";

    public static String HOME_REQUEST = "HOME";
    public static String MAGIC_PLAY_REQUEST = "MAGIC_PLAY";
    public static String FAV_REQUEST = "FAVOURITE";
    public static String SETTINGS_REQUEST = "SETTINGS";
    public static String PROFILE_REQUEST = "PROFILE";

    public static String proKey = "com.rocket.quizzy.pro.key";
    public static boolean isisFirstScreenLogin(Context context){
        return false;
    };


    public static void statusbarAccentColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.themeColor));
        }

    }

    public static void setLanguage(Context context,String language){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Global.LANGUAGE,language).apply();
    }

    public static void setNotification(Context context,boolean enable){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Global.RECEIVE_NOTIFICATION,enable).apply();
    }

    public static String getLanguage(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(Global.LANGUAGE,"English (US)");
    }

    public static boolean getNotification(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Global.RECEIVE_NOTIFICATION,false);
    }

    public static void setDarkMode(Context context,boolean enable){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (enable){
            editor.putBoolean(Global.DARK_MODE,true).apply();
        }else {
            editor.putBoolean(Global.DARK_MODE,false).apply();
        }
    }

    public static boolean isDarkMode(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Global.DARK_MODE,false);
    }

    public static void networkCheck(Context context){
        if (!isConnected(context)) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.no_internet_dialog_title), Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.no_internet_dialog_title))
                    .setMessage(R.string.no_internet_dialog_desc)
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.yes_msg), (dialog, which) -> context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)))
                    .setNegativeButton(context.getString(R.string.no_msg), (dialog, which) -> ((Activity) context).finish());
            //Creating dialog box
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public static boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)((Activity)context).getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", Objects.requireNonNull(e.getMessage()));
        }
        return false;
    }

    public static boolean isPro(Context context){
        SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return pref.getBoolean(Global.proKey,false);
    }

}
