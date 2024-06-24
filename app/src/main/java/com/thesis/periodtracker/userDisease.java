package com.thesis.periodtracker;

public class userDisease {
    private int id;
    private String DiseaseName, description;
    private boolean LifeThreatening;

    public userDisease(int id, String diseaseName, String description, boolean lifeThreatening) {
        this.id = id;
        DiseaseName = diseaseName;
        this.description = description;
        LifeThreatening = lifeThreatening;
    }

    public userDisease() {
        DiseaseName = null;
        this.description = null;
    }

    public int getId() {
        return id;
    }

    public String getDiseaseName() {
        return this.DiseaseName;
    }

    public boolean isLifeThreatening() {
        return this.LifeThreatening;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiseaseName(String diseaseName) {
        this.DiseaseName = diseaseName;
    }

    public void setLifeThreatening(boolean lifeThreatening) {
        this.LifeThreatening = lifeThreatening;
    }
}
