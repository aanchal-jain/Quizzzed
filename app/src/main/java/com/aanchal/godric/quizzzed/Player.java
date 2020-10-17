package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Player implements Serializable {
    @SerializedName("username")
    String username;

    @SerializedName("image")
    String image;

    @SerializedName("score")
    int score;

    @SerializedName("status")
    int status;

    @SerializedName("match_length")
    int match_length;

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public int getScore() {
        return score;
    }

    public int getStatus() {
        return status;
    }

    public int getMatch_length() {
        return match_length;
    }
}
