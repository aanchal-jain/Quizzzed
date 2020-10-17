package com.aanchal.godric.quizzzed;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitService extends Service {
    private static final String TAG = "WaitService";
    public static final String BROADCAST_ACTION = "com.aanchal.godric.quizzzed.displayevent";
    private final Handler handler = new Handler();
    Intent intent;
    public WaitService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent,int startId){
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI,500);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        @Override
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000);
        }
    };

    private void DisplayLoggingInfo() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        SharedPreferences preferences = getSharedPreferences("name",MODE_PRIVATE);
        String username = preferences.getString("username","");
        String game_code = preferences.getString("game_code","");

        Call<Players> call = apiInterface.getPlayers(username,game_code);
        call.enqueue(new Callback<Players>() {
            @Override
            public void onResponse(Call<Players> call, Response<Players> response) {
                Players players = response.body();
                ArrayList<Player> player = players.getPlayer();

                Bundle bundle = new Bundle();
                bundle.putSerializable("player",player);
                intent.putExtra("player",bundle);
                sendBroadcast(intent);
            }
            @Override
            public void onFailure(Call<Players> call, Throwable t) {

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
    }
}
