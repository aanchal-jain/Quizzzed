package com.aanchal.godric.quizzzed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuitGameActivity extends AppCompatActivity {


    private TextView score,coins;
    private Button main_menu,exit;
    private SharedPreferences preferences;
    private String username,game_code;

    private ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit_game);
        init();

        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuitGameActivity.this,MainMenuActivity.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void init() {
        score = findViewById(R.id.score);
        coins = findViewById(R.id.quit_added_coins);
        main_menu = findViewById(R.id.main_menu_button1);
        exit  = findViewById(R.id.exit1);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        preferences = getSharedPreferences("name",MODE_PRIVATE);
        username = preferences.getString("username","");
        game_code = preferences.getString("game_code","");

        Call<Stats> call = apiInterface.getScore(username,game_code);
        call.enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(Call<Stats> call, Response<Stats> response) {
                Stats stats = response.body();
                score.setText(""+stats.getScore());
                coins.setText("+ "+stats.getCoins());
            }
            @Override
            public void onFailure(Call<Stats> call, Throwable t) {

            }
        });

    }
    @Override
    public void onBackPressed(){
        final Dialog dialog = new Dialog(QuitGameActivity.this);
        dialog.setContentView(R.layout.exit_dialog);

        Button yes = dialog.findViewById(R.id.exit_dialog_yes);
        Button no = dialog.findViewById(R.id.exit_dialog_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
