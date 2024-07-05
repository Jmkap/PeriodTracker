package com.thesis.periodtracker;

public class userDisease {
    private int id;
    private int session_id;
    private String DiseaseName;

    public userDisease(int id, String diseaseName, String description, boolean lifeThreatening) {
        this.id = id;
        DiseaseName = diseaseName;
    }

    public userDisease() {
        DiseaseName = null;
    }

    public int getId() {
        return id;
    }

    public int getSession_id() {
        return session_id;
    }

    public String getDiseaseName() {
        return this.DiseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.DiseaseName = diseaseName;
    }

}
