package com.thesis.periodtracker;

import java.util.ArrayList;

public class UserInfoItem {
    private int userId, age;
    private String name, nickname;
    private ArrayList<userSymptom> userSymptoms;
    private ArrayList<userDisease> PreviousDisease;

    public UserInfoItem(int userId, int age, String name, String nickname) {
        this.userId = userId;
        this.age = age;
        this.name = name;
        this.nickname = nickname;
    }

    public UserInfoItem(int userId, int age, String name, String nickname, ArrayList<userSymptom> userSymptoms, ArrayList<userDisease> previousDisease) {
        this.userId = userId;
        this.age = age;
        this.name = name;
        this.nickname = nickname;
        this.userSymptoms = userSymptoms;
        PreviousDisease = previousDisease;
    }

    public int getUserId() {
        return userId;
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
        return PreviousDisease;
    }
}
