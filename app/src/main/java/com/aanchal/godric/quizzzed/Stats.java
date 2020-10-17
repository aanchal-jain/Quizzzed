package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

public class Stats {
    @SerializedName("totalMatches")
    String totalMatches;

    @SerializedName("matchesWon")
    String matchesWon;

    @SerializedName("score")
    int score;

    @SerializedName("coins")
    int coins;

    public int getScore() {
        return score;
    }

    public int getCoins() {
        return coins;
    }

    public String getTotalMatches() {
        return totalMatches;
    }

    public String getMatchesWon() {
        return matchesWon;
    }
}
