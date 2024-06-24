package com.thesis.periodtracker;


public class userSymptom {
    private String SymptomName, description;
    private int durationDays;

    public userSymptom(String description) {
        this.description = description;
    }

    public userSymptom(String symptomName, String description, int durationDays) {
        SymptomName = symptomName;
        this.description = description;
        this.durationDays = durationDays;
    }

    public userSymptom() {
        SymptomName = null;
        this.description = null;
    }

    public String getSymptomName(){
        return this.SymptomName;
    }

    public String getDescription(){
        return this.description;
    }

    public int getDurationDays(){
        return this.durationDays;
    }

    public void setSymptomName(String name){
        this.SymptomName = name;
    }

    public void setDescription(String desc){
        this.description = desc;
    }

    public void setDurationDays(int numDays){
        this.durationDays = numDays;
    }
}
