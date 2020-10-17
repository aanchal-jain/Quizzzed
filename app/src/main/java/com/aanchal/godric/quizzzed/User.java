package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

class User {
    @SerializedName("status")
    String status;

    public String getStatus(){
        return status;
    }
}
