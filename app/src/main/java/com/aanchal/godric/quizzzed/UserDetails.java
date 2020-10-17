package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetails implements Serializable {

    @SerializedName("image")
    String image;

    @SerializedName("coins")
    int coins;

    @SerializedName("totalMatches")
    int totalMatches;

    @SerializedName("matchesWon")
    int matchesWon;

    public String getImage() {
        return image;
    }

    public int getCoins() {
        return coins;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public int getMatchesWon() {
        return matchesWon;
    }
}
