package com.thesis.periodtracker.Rasa.responses;

import com.google.gson.annotations.SerializedName;

public class ImageResponse extends RasaResponse {
    @SerializedName("image")
    private String image;

    public String getImageUrl(){
        return image;
    }
}
