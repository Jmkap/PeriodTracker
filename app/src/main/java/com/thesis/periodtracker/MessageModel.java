package com.thesis.periodtracker;

import android.os.Message;

public class MessageModel {

    public static final int RECEIVED = 0;
    public static final int SENT = 1;
    private String message;
    private String date;
    private String timestamp;
    private int type;

    //============Constructors===============
    public MessageModel(String message, String date, String timestamp, int type) {
        this.message = message;
        this.date = date;
        this.timestamp = timestamp;
        this.type = type;
    }


    //============Getters and Setters=============
    public String getMessage() {
        return message;
    }

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
