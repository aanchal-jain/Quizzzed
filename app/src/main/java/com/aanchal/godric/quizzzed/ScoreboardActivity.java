package com.aanchal.godric.quizzzed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreboardActivity extends AppCompatActivity {

    public static final String TAG = "BroadcastTest";
    private Intent intent;

    private ImageView badge;
    int[] badge_img = {R.drawable.first,R.drawable.second,R.drawable.third,R.drawable.fourth,R.drawable.fifth};
    private TextView coins;
    private RecyclerView recyclerView;
    private Button main_menu,exit;
    int j=0;

    SharedPreferences preferences;
    ApiInterface apiInterface;
    ArrayList<Player> playerArrayList;
    private String username,game_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        init();

        //Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
        Call<Stats> call = apiInterface.getScore(username,game_code);
        call.enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(Call<Stats> call, Response<Stats> response) {
                Stats stats = response.body();
                //Toast.makeText(ScoreboardActivity.this, "score - "+stats.getScore(), Toast.LENGTH_SHORT).show();
                coins.setText("+ "+stats.getCoins());
            }

            @Override
            public void onFailure(Call<Stats> call, Throwable t) {

            }
        });

        Call<Players> playersCall = apiInterface.getScoreboard(username,game_code);
        playersCall.enqueue(new Callback<Players>() {
            @Override
            public void onResponse(Call<Players> call, Response<Players> response) {
                Players players = response.body();
                playerArrayList = players.getPlayer();
                //Toast.makeText(ScoreboardActivity.this, "Player- "+playerArrayList.get(0).getUsername(), Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter(new ScoreboardAdapter(ScoreboardActivity.this,playerArrayList));

                for(int i =0;i<playerArrayList.size();i++){
                    if(playerArrayList.get(i).getUsername().equals(username))
                        badge.setImageResource(badge_img[i]);
                }

            }

            @Override
            public void onFailure(Call<Players> call, Throwable t) {

            }
        });
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
                disconnect();
                startActivity(new Intent(ScoreboardActivity.this,MainMenuActivity.class));
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
                onBackPressed();
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        //startService(intent);
        registerReceiver(broadcastReceiver,new IntentFilter(ScoreBoardService.BROADCAST_ACTION));
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    private void updateUI(Intent intent) {
        Toast.makeText(this, "UpdateUi Call", Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getBundleExtra("player");
        playerArrayList = null;
        playerArrayList = (ArrayList<Player>) bundle.getSerializable("player");
        if(j == 0 || j < playerArrayList.size()){
            //Toast.makeText(this, "bundle "+bundle, Toast.LENGTH_SHORT).show();
            recyclerView.swapAdapter(new ScoreboardAdapter(this,playerArrayList),true);
            j = playerArrayList.size();
        }

        for(int i =0;i<playerArrayList.size();i++){
            if(playerArrayList.get(i).getUsername().equals(username))
                badge.setImageResource(badge_img[i]);
        }

    }

    private void init(){
        badge = findViewById(R.id.player_badge);
        coins = findViewById(R.id.scoreboard_added_coins);
        recyclerView = findViewById(R.id.recycler_scoreboard);
        main_menu = findViewById(R.id.main_menu_button2);
        exit = findViewById(R.id.exit2);
        preferences = getSharedPreferences("name",MODE_PRIVATE);
        username = preferences.getString("username","");
        game_code = preferences.getString("game_code","");

        intent = new Intent(this,ScoreBoardService.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(ScoreboardActivity.this));

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);



    }

    @Override
    public void onBackPressed(){
        setData();
        final Dialog dialog = new Dialog(ScoreboardActivity.this);
        dialog.setContentView(R.layout.exit_dialog);

        Button yes = dialog.findViewById(R.id.exit_dialog_yes);
        Button no = dialog.findViewById(R.id.exit_dialog_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
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

    public void disconnect(){
        Call<User> call = apiInterface.deleteConnection(username,game_code);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    public void setData(){
        for (int i = 0; i < playerArrayList.size(); i++) {
            if (playerArrayList.get(i).getUsername().equals(username)) {
                if (i == 0) {
                    Call<User> matchWonCall = apiInterface.setMatchWon(username);
                    matchWonCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });
                }
            }
        }
    }
}
