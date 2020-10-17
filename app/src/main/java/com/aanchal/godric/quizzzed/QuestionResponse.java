package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionResponse implements Serializable {

    @SerializedName("question")
    String question;

    @SerializedName("response")
    String response;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
