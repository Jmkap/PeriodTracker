package com.thesis.periodtracker.Rasa.responses;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.concurrent.locks.Condition;

public class RasaResponse {
    @SerializedName("recipient_id")
    private String recipientId;

    public String getRecipientId() {
        return recipientId;
    }
}
