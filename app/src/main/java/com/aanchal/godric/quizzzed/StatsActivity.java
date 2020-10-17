package com.aanchal.godric.quizzzed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsActivity extends AppCompatActivity {

    private TextView total_matches,matches_won;
    private ApiInterface apiInterface;
    SharedPreferences preferences;
    private String username,tot_matches,won;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        init();

        Call<Stats> call= apiInterface.fetchStats(username);
        call.enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(Call<Stats> call, Response<Stats> response) {
                Stats stats = response.body();
                tot_matches = stats.getTotalMatches();
                won = stats.getMatchesWon();

                total_matches.setText(tot_matches);
                matches_won.setText(won);
            }

            @Override
            public void onFailure(Call<Stats> call, Throwable t) {

            }
        });
    }

    private void init() {
        total_matches = findViewById(R.id.total_matches);
        matches_won = findViewById(R.id.matches_won);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        preferences = getSharedPreferences("name", MODE_PRIVATE);
        username = preferences.getString("username", "");
    }
}
