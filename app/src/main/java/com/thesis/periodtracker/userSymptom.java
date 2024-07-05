package com.thesis.periodtracker;


public class userSymptom {
    private String SymptomName, chatLog;
    private int durationDays, id;

    public userSymptom(String chatLog) {
        this.chatLog = chatLog;
    }

    public userSymptom(int id, String symptomName, String chatLog, int durationDays) {
        this.id = id;
        SymptomName = symptomName;
        this.chatLog = chatLog;
        this.durationDays = durationDays;
    }

    public userSymptom() {
        SymptomName = null;
        this.chatLog = null;
    }

    public int getId() {
        return id;
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

    public void setSymptomName(String name){
        this.SymptomName = name;
    }

    public void setChatLog(String chatLog){
        this.chatLog = chatLog;
    }

    public void setDurationDays(int numDays){
        this.durationDays = numDays;
    }
}
