package com.thesis.periodtracker.UserModels;

import java.util.ArrayList;

public class UserInfoItem {
    private int age;
    private String name;
    private boolean menopause;

    public UserInfoItem(int age, String name, boolean menopause) {
        this.age = age;
        this.name = name;
        this.menopause = menopause;
    }

    public int getAge() {
        return age;
    }
    public String getName() {
        return name;
    }
    public boolean isMenopause() { return this.menopause; }
}
