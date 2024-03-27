package com.example.kanban_board_java.app;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.example.kanban_board_java.utils.PreferenceUtils;

import dagger.hilt.android.HiltAndroidApp;
import javax.inject.Inject;

@HiltAndroidApp
public class KanbanBoardApp extends Application {

    private Activity activity;

    @Inject
    PreferenceUtils prefsUtils;

    public boolean isShowOpenAds = true;

    private static KanbanBoardApp instance;

    public static KanbanBoardApp getAppInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}