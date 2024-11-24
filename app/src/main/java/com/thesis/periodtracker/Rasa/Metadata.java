package com.thesis.periodtracker.Rasa;

import com.google.gson.annotations.SerializedName;

public class Metadata {
    @SerializedName("username")
    private String username;

    @SerializedName("age")
    private int age;

    @SerializedName("isMenopause")
    private boolean isMenopause;

    public Metadata(String username, int age, boolean isMenopause) {
        this.username = username;
        this.age = age;
        this.isMenopause = isMenopause;
    }

    // Getters
    public String getUsername() { return username; }
    public int getAge() { return age; }
    public boolean isMenopause() { return isMenopause; }
}