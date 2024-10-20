package com.thesis.periodtracker;

import java.util.ArrayList;

public class UserInfoItem {
    private int age;
    private String name, nickname;
    private ArrayList<userSymptom> userSymptoms;
    private ArrayList<userDisease> PreviousDiseases;
    private boolean puberty;
    private boolean menopause;

    public UserInfoItem(int age, String name, String nickname, boolean puberty, boolean menopause) {
        this.age = age;
        this.name = name;
        this.nickname = nickname;
        this.menopause = menopause;
        this.puberty = puberty;
    }

    public UserInfoItem(int age, String name, String nickname, boolean menopause) {
        this.age = age;
        this.name = name;
        this.nickname = nickname;
        this.menopause = menopause;
    }

    public UserInfoItem(int age, String name, String nickname, ArrayList<userSymptom> userSymptoms, ArrayList<userDisease> previousDisease, boolean menopause) {
        this.age = age;
        this.name = name;
        this.nickname = nickname;
        this.userSymptoms = userSymptoms;
        PreviousDiseases = previousDisease;
        this.menopause = menopause;
    }

    public UserInfoItem(int age, String name, String nickname, ArrayList<userSymptom> userSymptoms, ArrayList<userDisease> previousDisease, boolean puberty, boolean menopause) {
        this.age = age;
        this.name = name;
        this.nickname = nickname;
        this.userSymptoms = userSymptoms;
        PreviousDiseases = previousDisease;
        this.menopause = menopause;
        this.puberty = puberty;
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
    public boolean isPuberty() { return this.puberty; }
    public boolean isMenopause() { return this.menopause; }
}
