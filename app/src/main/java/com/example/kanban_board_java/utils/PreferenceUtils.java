package com.example.kanban_board_java.utils;

import android.content.SharedPreferences;

import com.example.kanban_board_java.app.AppConstant;

public class PreferenceUtils {

    private SharedPreferences sharedPreferences;

    public PreferenceUtils(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean isLogin() {
        return sharedPreferences.getBoolean(AppConstant.LOGIN_KEY, false);
    }

    public void setLogin(boolean login) {
        sharedPreferences.edit().putBoolean(AppConstant.LOGIN_KEY, login).apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(AppConstant.USER_KEY, "");
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString(AppConstant.USER_KEY, userId).apply();
    }
}
