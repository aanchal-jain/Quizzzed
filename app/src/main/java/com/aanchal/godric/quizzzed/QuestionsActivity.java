package com.aanchal.godric.quizzzed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionsActivity extends AppCompatActivity {

    private TextView timer;
    private Button help,quit;
    private ImageView next_ques,prev_ques,finish_img;
    private TextView question,finish;
    private RadioButton option1,option2,option3,option4;
    private CountDownTimer countDownTimer;

    private int match_length,counter_time,resumeCounterTime,minutes,seconds;
    private int question_counter = 0,score = 0,coins = 0;
    private String game_code,username,answer;

    private SharedPreferences preferences;
    private ApiInterface apiInterface;
    private QuestionsList questionsList;
    private ArrayList<Questions> questions;
    private ArrayList<QuestionResponse> questionResponses;
    private Questions questionsObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        init();

        //Toast.makeText(this, ""+counter_time, Toast.LENGTH_SHORT).show();

        //help dialog
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                    countDownTimer.cancel();
                    final Dialog dialog = new Dialog(QuestionsActivity.this);
                    dialog.setContentView(R.layout.help_dialog);

                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            resumeTimer(minutes,seconds);
                        }
                    });

                    Button yes = dialog.findViewById(R.id.help_dialog_yes);
                    Button no = dialog.findViewById(R.id.help_dialog_no);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Call<User> call = apiInterface.deductCoins(username);
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    User user = response.body();
                                    String status = user.getStatus();
                                    if(status.equals("Failure")){
                                        dialog.dismiss();
                                        Toast.makeText(QuestionsActivity.this, "You don't have enough coins.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        setQuestionResponses(answer);
                                        dialog.dismiss();
                                        question_counter++;
                                        setQuestion();
                                        resumeTimer(minutes,seconds);
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            resumeTimer(minutes,seconds);
                        }
                    });
                    dialog.show();
                    }
        });
        //quit dialog
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        next_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question_counter++;
                setQuestion();
            }
        });
        prev_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question_counter--;
                setQuestion();
            }
        });
        finish_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendScore();
                startActivity(new Intent(QuestionsActivity.this,ScoreboardActivity.class));
            }
        });
    }

    private void init() {
        timer = findViewById(R.id.timer);
        question = findViewById(R.id.question);
        prev_ques = findViewById(R.id.prev_question);
        next_ques = findViewById(R.id.next_question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        finish_img = findViewById(R.id.finish_button);
        finish = findViewById(R.id.finish_txt);
        help = findViewById(R.id.help_me);
        quit = findViewById(R.id.quit_game);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        preferences = getSharedPreferences("name", Context.MODE_PRIVATE);
        username = preferences.getString("username","");
        game_code = preferences.getString("game_code","");

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Fetching questions ..");
        pd.show();
        //get questions
        Call<QuestionsList> questionsListCall = apiInterface.getQuestions(game_code);
        questionsListCall.enqueue(new Callback<QuestionsList>() {
            @Override
            public void onResponse(Call<QuestionsList> call, Response<QuestionsList> response) {
                questionsList = response.body();
                questions = questionsList.getQuestions();
                //Toast.makeText(QuestionsActivity.this, "Question sSize "+questions.size(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
                setQuestion();

            }

            @Override
            public void onFailure(Call<QuestionsList> call, Throwable t) {
                Toast.makeText(QuestionsActivity.this, "Error "+t, Toast.LENGTH_SHORT).show();
            }
        });

        Call<Player> getMatchLength = apiInterface.getMatchLength(game_code);
        getMatchLength.enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                Player player = response.body();
                match_length = player.getMatch_length();
                //Toast.makeText(QuestionsActivity.this, ""+match_length, Toast.LENGTH_SHORT).show();
                //get Match Duration
                switch(match_length){
                    case 10:counter_time = 90000;
                        break;
                    case 20:counter_time = 180000;
                        break;
                    case 30:counter_time = 300000;
                        break;
                }
                //timer
                //Toast.makeText(QuestionsActivity.this, "Timer "+counter_time, Toast.LENGTH_SHORT).show();
                countDownTimer = new CountDownTimer(counter_time,1000) {

                    public void onTick(long millisUntilFinished) {
                        minutes = (int) (millisUntilFinished/60000);
                        seconds = (int) ((millisUntilFinished%60000)/1000);
                        if(seconds<10)
                            timer.setText("0"+minutes+":0"+seconds);
                        else
                            timer.setText("0"+minutes+":"+seconds);
                        if(minutes == 0 && seconds<=10)
                            timer.setBackgroundColor(Color.parseColor("#9FD30000"));
                    }

                    public void onFinish() {
                        sendScore();
                        final Dialog dialog = new Dialog(QuestionsActivity.this);
                        dialog.setContentView(R.layout.timeup);
                        dialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(QuestionsActivity.this,ScoreboardActivity.class);
                                startActivity(intent);
                            }
                        },3000);
                    }
                }.start();

            }

            @Override
            public void onFailure(Call<Player> call, Throwable t) {
                Toast.makeText(QuestionsActivity.this, "Error "+t, Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public void onBackPressed(){
            countDownTimer.cancel();
            final Dialog dialog = new Dialog(QuestionsActivity.this);
            dialog.setContentView(R.layout.quit_dialog);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    resumeTimer(minutes,seconds);
                }
            });

            Button yes = dialog.findViewById(R.id.quit_dialog_yes);
            Button no = dialog.findViewById(R.id.quit_dialog_no);

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendScore();
                    Intent intent = new Intent(QuestionsActivity.this,QuitGameActivity.class);
                    startActivity(intent);
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    resumeTimer(minutes,seconds);
                }
            });
            dialog.show();
    }

    // set Question
    private void setQuestion() {
        if(question_counter == match_length-1){
            finish.setVisibility(View.VISIBLE);
            finish_img.setVisibility(View.VISIBLE);
            next_ques.setVisibility(View.GONE);
            //Toast.makeText(QuestionsActivity.this,"This is the last question",Toast.LENGTH_SHORT).show();
        }else
            next_ques.setVisibility(View.VISIBLE);

        if(question_counter == 0)
            prev_ques.setVisibility(View.GONE);
        else
            prev_ques.setVisibility(View.VISIBLE);

        //Toast.makeText(this, "Size ------ "+questions.size(), Toast.LENGTH_SHORT).show();
        questionsObj = questions.get(question_counter);
        answer = questionsObj.getAnswer();

        question.setText(questionsObj.getQuestion());
        option1.setText(questionsObj.getOption1());
        option2.setText(questionsObj.getOption2());
        option3.setText(questionsObj.getOption3());
        option4.setText(questionsObj.getOption4());

        String ans="";
        if(questionResponses !=null && questionResponses.size() > question_counter&& questionResponses.get(question_counter)!=null) {
                ans = questionResponses.get(question_counter).getResponse();
            if (ans.equals(questionsObj.getOption1())) {
                option1.setChecked(true);
                option2.setChecked(false);
                option3.setChecked(false);
                option4.setChecked(false);
            }else if (ans.equals(questionsObj.getOption2())) {
                option2.setChecked(true);
                option1.setChecked(false);
                option3.setChecked(false);
                option4.setChecked(false);
            }
            else if (ans.equals(questionsObj.getOption3())){
                option3.setChecked(true);
                option1.setChecked(false);
                option2.setChecked(false);
                option4.setChecked(false);
            }
            else if (ans.equals(questionsObj.getOption4())){
                option4.setChecked(true);
                option1.setChecked(false);
                option2.setChecked(false);
                option3.setChecked(false);
            }else{
                option1.setChecked(false);
                option2.setChecked(false);
                option3.setChecked(false);
                option4.setChecked(false);

            }
        }/*else{
            option1.setChecked(false);
            option2.setChecked(false);
            option3.setChecked(false);
            option4.setChecked(false);

        }*/
    }

    public void sendResponse(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.option1 :
                if(checked)
                    setQuestionResponses(option1.getText().toString());
                break;
            case R.id.option2 :
                if(checked)
                    setQuestionResponses(option2.getText().toString());
                break;
            case R.id.option3 :
                if(checked)
                    setQuestionResponses(option3.getText().toString());
                break;
            case R.id.option4 :
                if(checked)
                    setQuestionResponses(option4.getText().toString());
        }
    }

    private void setQuestionResponses(String response){
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setResponse(response);
        if(questionResponses == null)
            questionResponses = new ArrayList<>();
        if(questionResponses.size() > question_counter && questionResponses.get(question_counter)!=null){
            questionResponses.remove(question_counter);
        }
        if(questionResponses.size() <= question_counter){
            for(int i = questionResponses.size() ;i < question_counter;i++){
                QuestionResponse questionResponse1 = new QuestionResponse();
                questionResponse1.setResponse("");
                questionResponses.add(i,questionResponse);
            }
        }
        questionResponses.add(question_counter,questionResponse);
    }

    // resume timer
    public void resumeTimer(int min,int sec){
        resumeCounterTime = (min*60000)+(sec*1000);
        countDownTimer = new CountDownTimer(resumeCounterTime,1000) {

            public void onTick(long millisUntilFinished) {
                minutes = (int) (millisUntilFinished/60000);
                seconds = (int) ((millisUntilFinished%60000)/1000);
                if(seconds<10)
                    timer.setText("0"+minutes+":0"+seconds);
                else
                    timer.setText("0"+minutes+":"+seconds);
                if(minutes == 0 && seconds<=10)
                    timer.setBackgroundColor(Color.parseColor("#9FD30000"));
            }

            public void onFinish() {
                sendScore();
                final Dialog dialog = new Dialog(QuestionsActivity.this);
                dialog.setContentView(R.layout.timeup);
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(QuestionsActivity.this,ScoreboardActivity.class);
                        startActivity(intent);
                    }
                },2000);
            }
        }.start();
    }

    private void sendScore(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(questionResponses!=null){
            for(int i = 0;i<questionResponses.size();i++){
                if(questions.get(i).getAnswer().equals(questionResponses.get(i).getResponse()))
                    score++;
            }
        }
        //Toast.makeText(this, "Score-"+score, //Toast.LENGTH_SHORT).show();
        Call<User> call = apiInterface.sendScore(username,score,game_code);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

}

