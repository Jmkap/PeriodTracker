package com.thesis.periodtracker;

import android.os.Message;

public class MessageModel {

    private String message;
    private int picture;

    //============Constructors===============
    public  MessageModel(String message, int picture) {
        this.message = message;
        this.picture = picture;
    }

    public MessageModel(String message) {
        this.message = message;
    }

    //============Getters and Setters=============
    public String getMessage() {
        return this.message;
    }

    public int getPicture() {
        return this.picture;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}
