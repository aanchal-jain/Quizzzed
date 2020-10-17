package com.aanchal.godric.quizzzed;

import com.google.gson.annotations.SerializedName;

public class ImageBean {
    @SerializedName("response")
    String response;

    String path;

    public String getResponse() {
        return response;
    }

    public String getPath() {
        return path;
    }
}
