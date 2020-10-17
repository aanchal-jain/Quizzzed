package com.aanchal.godric.quizzzed;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartGameActivity extends AppCompatActivity {

    public static final String TAG = "BroadcastTest";
    private Intent intent;

    private TextView secret_code,txt_joined_ppl,txt_match_length;
    private CardView players_card;
    private RecyclerView players_list;
    private LinearLayout options_layout;
    private RadioButton length10,length20,length30;
    private Button start_game;

    private int match_length;
    private String game_code,username;
    private SharedPreferences preferences;
    private ApiInterface apiInterface;

    private ArrayList<Player> player;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        init();
        generateSecretCode();

        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> statusCall = apiInterface.setStatus(game_code);
                statusCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    }
                });
                Call<User> call = apiInterface.setMatchLength(game_code,match_length);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    }
                });
                Call<User> call1 = apiInterface.setQuestions(game_code,match_length);
                call1.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(StartGameActivity.this,""+t,Toast.LENGTH_LONG).show();
                    }
                });

                Intent intent = new Intent(StartGameActivity.this,GameCountdownActivity.class);
                /*Bundle bundle = new Bundle();
                bundle.putInt("match_length",match_length);
                bundle.putString("game_code",game_code);
                intent.putExtra("game_details",bundle);*/
                startActivity(intent);
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
            /*txt_joined_ppl.setVisibility(View.VISIBLE);
            txt_match_length.setVisibility(View.VISIBLE);
            players_card.setVisibility(View.VISIBLE);
            options_layout.setVisibility(View.VISIBLE);
            start_game.setVisibility(View.VISIBLE);*/

        }
        if(player.size()>0){
            txt_joined_ppl.setVisibility(View.VISIBLE);
            txt_match_length.setVisibility(View.VISIBLE);
            players_card.setVisibility(View.VISIBLE);
            options_layout.setVisibility(View.VISIBLE);
            start_game.setVisibility(View.VISIBLE);
        }
    }

    private void init(){
        secret_code = findViewById(R.id.game_code);
        txt_joined_ppl = findViewById(R.id.joined_ppl_txt);
        players_card = findViewById(R.id.start_game_card);
        txt_match_length = findViewById(R.id.txt_match_length);
        options_layout = findViewById(R.id.options_layout);
        players_list = findViewById(R.id.recycler_players);
        length10 = findViewById(R.id.match_length_10);
        length20 = findViewById(R.id.match_length_20);
        length30 = findViewById(R.id.match_length_30);
        start_game = findViewById(R.id.start_game);

        players_list.setLayoutManager(new LinearLayoutManager(this));

        preferences = getSharedPreferences("name",MODE_PRIVATE);
        username = preferences.getString("username","");

        intent = new Intent(this,WaitService.class);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

    }
    private void generateSecretCode() {
        String arr1[] = {"near", "able", "rich", "safe", "hard", "high", "down", "long", "born", "late",
                "sure", "open", "home", "evil", "dark", "many", "like", "east", "good", "zero", "sick",
                "best", "lost", "free", "thin", "soft", "full", "even", "ugly", "true", "nice", "cold",
                "fair", "past", "fast", "real", "west", "fine", "mini", "dead", "wild", "tall", "easy",
                "kind", "well", "lean", "deep", "same", "cool", "loud", "wise", "pure", "last", "epic",
                "half", "bold", "calm", "warm", "holy", "bare", "slow", "sent", "flat", "sane", "peak",
                "busy", "rude", "sour", "foul", "just", "idle", "deaf", "tidy", "dull"};
        String arr2[] = {"life","love","ring","wolf", "fish", "five", "king", "tree", "over", "time", "able",
                "have", "sing", "star", "city", "soul", "rich", "duck", "foot", "film", "lion", "pain", "rain",
                "iron", "ball", "fire", "wood", "care", "cake", "lady", "work", "mole", "moon", "golf", "land",
                "blue", "rock", "rose", "wish", "home", "line", "hand", "food", "hope", "wind", "bird", "bean",
                "hair", "room", "fall", "hero", "bell", "leaf", "list", "math", "ship", "edge", "oven", "pear",
                "desk", "gate", "bear", "taco", "band", "sand", "snow", "gift", "bush", "mark", "corn", "card",
                "book", "town","gold", "dish", "rage", "hood", "worm", "poem", "ding"};
        int pos1 = (int)(Math.random()*arr1.length);
        int pos2 = (int)(Math.random()*arr2.length);
        game_code = arr1[pos1] +" "+arr2[pos2];
        secret_code.setText(game_code);
        preferences.edit().putString("game_code",game_code);

        //insert code in table codes
        Call<User> call1 = apiInterface.insertCode(username,game_code);
        call1.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                preferences.edit().putString("game_code",game_code).commit();
                return;
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                start_game.setEnabled(false);
            }
        });

        //insert user in connections
        Call<User> call2 = apiInterface.makeConnection(game_code,username);
        call2.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                Toast.makeText(StartGameActivity.this, "Game Start response "+user, Toast.LENGTH_SHORT).show();
                startService(intent);
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    public void getMatchLength(View view){
        start_game.setEnabled(true);
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.match_length_10: if (checked){
                                        match_length = 10;}
                                        break;
            case R.id.match_length_20: if (checked){
                                        match_length = 20;}
                                        break;
            case R.id.match_length_30: if(checked){
                                        match_length = 30;}
                                        break;
            }
        }
    @Override
    public void onBackPressed(){
        Call<User> call = apiInterface.deleteUser(username,game_code);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
        startActivity(new Intent(StartGameActivity.this,MainMenuActivity.class));
    }
}


