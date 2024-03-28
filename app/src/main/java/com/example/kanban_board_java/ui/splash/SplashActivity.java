package com.example.kanban_board_java.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.kanban_board_java.app.AppConstant;
import com.example.kanban_board_java.app.BaseActivity;
import com.example.kanban_board_java.databinding.ActivitySplashBinding;
import com.example.kanban_board_java.ui.home.HomeActivity;
import com.example.kanban_board_java.ui.login.LoginActivity;

import dagger.hilt.android.AndroidEntryPoint;

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsUtils.setUserId("SIVmFx5GaLcErR9I47mv7gv8yPs2");
        startTimer();
    }

    private void startTimer() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (prefsUtils.isLogin()) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.putExtra(AppConstant.LOGIN_KEY, 0);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
