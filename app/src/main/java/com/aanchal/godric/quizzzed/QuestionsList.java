package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QuestionsList {

    @SerializedName("questions")
    ArrayList<Questions> questions;

    public ArrayList<Questions> getQuestions() {
        return questions;
    }


}
