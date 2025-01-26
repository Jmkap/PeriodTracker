package com.thesis.periodtracker.RecyclerView;

import android.os.Message;

public class MessageModel {

    public static final int RECEIVED = 0;
    public static final int SENT = 1;
    private String message;
    private String imageUrl;
    private String date;
    private String timestamp;
    private int type;

    //============Constructors===============
    public MessageModel(String message, String imageUrl, String date, String timestamp, int type) {
        this.message = message;
        this.imageUrl = imageUrl;
        this.date = date;
        this.timestamp = timestamp;
        this.type = type;
    }


    //============Getters and Setters=============
    public String getMessage() {
        return message;
    }
    public String getImageUrl() { return imageUrl;}

    public String getDate() {
        return date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }
}
