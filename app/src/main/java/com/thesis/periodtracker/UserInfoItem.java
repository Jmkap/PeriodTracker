package com.thesis.periodtracker;

import java.util.ArrayList;

public class UserInfoItem {
    private int age;
    private String name, nickname;
    private ArrayList<userSymptom> userSymptoms;
    private ArrayList<userDisease> PreviousDiseases;

    public UserInfoItem(int age, String name, String nickname) {
        this.age = age;
        this.name = name;
        this.nickname = nickname;
    }

    public UserInfoItem(int age, String name, String nickname, ArrayList<userSymptom> userSymptoms, ArrayList<userDisease> previousDisease) {
        this.age = age;
        this.name = name;
        this.nickname = nickname;
        this.userSymptoms = userSymptoms;
        PreviousDiseases = previousDisease;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<userSymptom> getUserSymptoms() {
        return userSymptoms;
    }

    public ArrayList<userDisease> getPreviousDisease() {
        return PreviousDiseases;
    }
}
