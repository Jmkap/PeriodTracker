package com.thesis.periodtracker;

public class userDiagnosis {
    private String DiseaseName;
    private int rank, id;
    private float score;

    public userDiagnosis(String diseaseName, int rank, int id, float score) {
        DiseaseName = diseaseName;
        this.rank = rank;
        this.id = id;
        this.score = score;
    }

    public String getDiseaseName() {
        return DiseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        DiseaseName = diseaseName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
