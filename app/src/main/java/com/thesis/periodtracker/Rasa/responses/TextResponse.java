package com.thesis.periodtracker.Rasa.responses;

import com.google.gson.annotations.SerializedName;

// Text response subclass
public class TextResponse extends RasaResponse {
    @SerializedName("text")
    private String text;

    public String getText() {
        return text;
    }
}
