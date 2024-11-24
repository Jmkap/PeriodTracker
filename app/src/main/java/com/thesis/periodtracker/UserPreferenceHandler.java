package com.thesis.periodtracker;

import android.content.Context;
import android.content.SharedPreferences;

import com.thesis.periodtracker.UserModels.UserInfoItem;

public class UserPreferenceHandler {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_AGE = "age";
    private static final String KEY_IN_MENOPAUSE = "inMenopause";
    private static final String KEY_FIRST_TIME = "isFirstTime";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static UserPreferenceHandler instance;

    private UserPreferenceHandler(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static synchronized UserPreferenceHandler getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferenceHandler(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUserInfo(UserInfoItem user_info) {
        editor.putString(KEY_USERNAME, user_info.getName());
        editor.putInt(KEY_AGE, user_info.getAge());
        editor.putBoolean(KEY_IN_MENOPAUSE, user_info.isMenopause());
        editor.putBoolean(KEY_FIRST_TIME, false);  // Once we save user info, it's no longer first time
        editor.apply();
    }

    public void testSave(String username, int age, boolean inMenopause){
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_AGE, age);
        editor.putBoolean(KEY_IN_MENOPAUSE, inMenopause);
        editor.putBoolean(KEY_FIRST_TIME, false);  // Once we save user info, it's no longer first time
        editor.apply();
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    public int getAge() {
        return preferences.getInt(KEY_AGE, 0);
    }

    public boolean isInMenopause() {
        return preferences.getBoolean(KEY_IN_MENOPAUSE, false);
    }

    public boolean isFirstTimeUser() {
        return preferences.getBoolean(KEY_FIRST_TIME, true);
    }

    public void clearUserData() {
        editor.clear();
        editor.putBoolean(KEY_FIRST_TIME, true);  // Reset to first time user
        editor.apply();
    }
}
