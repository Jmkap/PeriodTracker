package com.thesis.periodtracker.Rasa;

public class RasaRequest {
    private String sender;
    private String message;
    private Metadata metadata;

    // Constructor for regular messages
    public RasaRequest(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.metadata = null;
    }

    // Constructor for messages with metadata
    public RasaRequest(String sender, String message, String username, int age, boolean isMenopause) {
        this.sender = sender;
        this.message = message;
        this.metadata = new Metadata(username, age, isMenopause);
    }

    // Getters
    public String getSender() { return sender; }
    public String getMessage() { return message; }
    public Metadata getMetadata() { return metadata; }
}