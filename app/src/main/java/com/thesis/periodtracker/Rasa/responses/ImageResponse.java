package com.thesis.periodtracker.Rasa.responses;

import com.google.gson.annotations.SerializedName;

public class ImageResponse extends RasaResponse {
    @SerializedName("text")
    private String text;
    @SerializedName("image")
    private String imageUrl;

    public String getText() {
        return text;
    }

    public String getImageUrl(){
        return imageUrl;
    }
}
