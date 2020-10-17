package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class Players {
    @SerializedName("players")
    ArrayList<Player> player;

    public ArrayList<Player> getPlayer() {
        return player;
    }
}
