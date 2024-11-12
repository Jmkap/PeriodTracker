package com.thesis.periodtracker.Rasa;

public class RasaRequest {
    private String sender;
    private String message;

    public RasaRequest(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}