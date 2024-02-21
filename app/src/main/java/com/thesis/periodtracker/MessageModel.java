package com.thesis.periodtracker;

import android.os.Message;

public class MessageModel {

    private final int RECEIVED = 0;
    private final int SENT = 1;
    private String message;
    private String date;
    private int type;
    private int picture;

    //============Constructors===============
    public  MessageModel(String message, int picture, String date, int type) {
        this.message = message;
        this.picture = picture;
        this.date = date;
        this.type = type;
    }

    public MessageModel(String message, int type) {
        this.type = type;
        this.message = message;
    }

    //============Getters and Setters=============
    public int getType() { return this.type; }

    public String getMessage() {
        return this.message;
    }
    public String getDate() { return this.date; };

    public int getPicture() {
        return this.picture;
    }

    public void setType(int type) { this.type = type; }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}
