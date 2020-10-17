package com.aanchal.godric.quizzzed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionPlan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        checkConnection();
        preferences = getSharedPreferences("name", Context.MODE_PRIVATE);
        username = preferences.getString("username","");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(username!="")
                    startActivity(new Intent(SplashScreenActivity.this,MainMenuActivity.class));
                else
                    startActivity(new Intent(SplashScreenActivity.this,SetUpProfileActivity.class));
            }
        },3000);
    }
    private void checkConnection() {
        if(!isOnline()){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.splash_layout),"No Internet Connection",Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }
}
