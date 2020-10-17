package com.aanchal.godric.quizzzed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.PendingIntent.getActivity;

public class MainMenuActivity extends AppCompatActivity {


    private LinearLayout linearLayout;
    private TextView coins;
    private CircleImageView profile_icon;
    private Button start_game,join_game,how_to_play,about_us,exit;

    private String username,image_path;
    private int int_coins;
    private ArrayList<UserDetails> userDetailsArrayList;

    private SharedPreferences preferences;
    private ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        init();
        checkConnection();

        profile_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this,ProfilePageActivity.class);
                intent.putExtra("img_path",image_path);
                startActivity(intent);
            }
        });
        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this,StartGameActivity.class));
            }
        });

        join_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this,JoinTheGameActivity.class));
            }
        });
        how_to_play.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this,HowToPlayActivity.class));
            }
        }));
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this,AboutUsActivity.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void checkConnection() {
        if(!isOnline()){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_menu_layout),"No Internet Connection",Snackbar.LENGTH_LONG);
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

    private void init() {
        linearLayout = findViewById(R.id.main_menu_layout);
        coins = findViewById(R.id.coins_value);
        profile_icon = findViewById(R.id.profile_icon);
        start_game = findViewById(R.id.start_game);
        join_game = findViewById(R.id.join_game);
        how_to_play = findViewById(R.id.how_to_play);
        about_us = findViewById(R.id.about_us);
        exit = findViewById(R.id.exit);

        preferences = getSharedPreferences("name",MODE_PRIVATE);
        username = preferences.getString("username","");

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        setData();

    }

    private void setData() {
        Call<UserDetailsList> call = apiInterface.getInfo(username);
        call.enqueue(new Callback<UserDetailsList>() {
            @Override
            public void onResponse(Call<UserDetailsList> call, Response<UserDetailsList> response) {
                UserDetailsList userDetailsList = response.body();
                userDetailsArrayList = userDetailsList.getUsers();
                image_path =userDetailsArrayList.get(0).getImage();
                int_coins = userDetailsArrayList.get(0).getCoins();
                Picasso.get().load(image_path).into(profile_icon);
                coins.setText(""+int_coins);
            }

            @Override
            public void onFailure(Call<UserDetailsList> call, Throwable t) {
                Toast.makeText(MainMenuActivity.this,"Data load Failed.",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed(){
        final Dialog dialog = new Dialog(MainMenuActivity.this);
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
