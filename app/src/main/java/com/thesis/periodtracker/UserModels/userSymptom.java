package com.thesis.periodtracker.UserModels;


public class userSymptom {
    private String SymptomName, chatLog;
    private int durationDays;

    public userSymptom(String chatLog) {
        this.chatLog = chatLog;
    }
    private int intensity;

    public userSymptom(String symptomName, int durationDays, int intensity) {
        this.SymptomName = symptomName;
        // this.chatLog = chatLog;
        this.durationDays = durationDays;
        this.intensity = intensity;
    }
    public String getSymptomName(){
        return this.SymptomName;
    }

    public String getChatLog(){
        return this.chatLog;
    }

    public int getDurationDays(){
        return this.durationDays;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setSymptomName(String name){
        this.SymptomName = name;
    }

    public void setChatLog(String chatLog){
        this.chatLog = chatLog;
    }

    public void setDurationDays(int numDays){
        this.durationDays = numDays;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
}
