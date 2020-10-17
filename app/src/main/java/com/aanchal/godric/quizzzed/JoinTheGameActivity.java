package com.aanchal.godric.quizzzed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinTheGameActivity extends AppCompatActivity {

    public static final String TAG = "BroadcastTest";
    private Intent intent;

    private EditText secret_code;
    private Button submit;
    private RecyclerView players_list;
    private TextView joined_ppl,game_starts_soon;
    private CardView card_players_list;

    private String username,game_code="";
    private ArrayList<Player> player;

    private SharedPreferences preferences;
    private ApiInterface apiInterface;

    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_the_game);
        init();

        secret_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(secret_code.getText().toString().length()==4) {
                    secret_code.setText(secret_code.getText()+" ");
                    secret_code.setSelection(5);
                }
                else if(secret_code.getText().toString().length()==9)
                    submit.setEnabled(true);

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                game_code = secret_code.getText().toString();
                //Toast.makeText(JoinTheGameActivity.this, "Code - "+game_code, Toast.LENGTH_SHORT).show();
                Call<User> call = apiInterface.makeConnection(game_code,username);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        String result = user.getStatus();
                        //Toast.makeText(JoinTheGameActivity.this, "Result : "+result, Toast.LENGTH_SHORT).show();
                        if(result.equals("Success")){
                            preferences.edit().putString("game_code",game_code).commit();
                            startService(intent);
                        }
                        else{
                            Toast.makeText(JoinTheGameActivity.this,"Invalid Secret Code",Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(JoinTheGameActivity.this,""+t,Toast.LENGTH_LONG).show();
                    }
                });
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
        registerReceiver(broadcastReceiver,new IntentFilter(WaitService.BROADCAST_ACTION));
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    private void updateUI(Intent intent){
        Bundle bundle = intent.getBundleExtra("player");
        player = null;
        player = (ArrayList<Player>) bundle.getSerializable("player");

        if(i==0 || i < player.size()){
            //Toast.makeText(this, "bundle "+bundle, Toast.LENGTH_SHORT).show();
            players_list.swapAdapter(new PlayersListAdapter(this,player),true);
            i = player.size();
        }
        if(player.size()>0){
            game_starts_soon.setVisibility(View.VISIBLE);
            joined_ppl.setVisibility(View.VISIBLE);
            card_players_list.setVisibility(View.VISIBLE);
        }
        if(player.size()>0 && player.get(0).getStatus()==1){
            startActivity(new Intent(JoinTheGameActivity.this,GameCountdownActivity.class));
        }
    }

    private void init() {
        secret_code = findViewById(R.id.join_game_code);
        submit = findViewById(R.id.submit_code);
        players_list  = findViewById(R.id.join_players);
        joined_ppl = findViewById(R.id.txt_joined_ppl);
        card_players_list = findViewById(R.id.list_joined_players);
        game_starts_soon = findViewById(R.id.txt_game_starts_soon);

        players_list.setLayoutManager(new LinearLayoutManager(this));

        preferences = getSharedPreferences("name",MODE_PRIVATE);
        username = preferences.getString("username","");

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        intent = new Intent(this,WaitService.class);
    }

    @Override
    public void onBackPressed(){
        if(!game_code.equals("")){
            Call<User> call = apiInterface.deleteUser(username,game_code);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    startActivity(new Intent(JoinTheGameActivity.this,MainMenuActivity.class));
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
        else
            startActivity(new Intent(JoinTheGameActivity.this,MainMenuActivity.class));
    }
}
