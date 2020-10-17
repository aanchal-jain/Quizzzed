package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserDetailsList {
    @SerializedName("users")
    ArrayList<UserDetails> users;

    public ArrayList<UserDetails> getUsers() {
        return users;
    }
}
