package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Questions implements Serializable {
    @SerializedName("id")
    String id;

    @SerializedName("question")
    String question;

    @SerializedName("option1")
    String option1;

    @SerializedName("option2")
    String option2;

    @SerializedName("option3")
    String option3;

    @SerializedName("option4")
    String option4;

    @SerializedName("answer")
    String answer;

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getAnswer() {
        return answer;
    }
}
