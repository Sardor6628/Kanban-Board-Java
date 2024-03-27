package com.example.kanban_board_java.app;

import android.os.Bundle;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;
import com.example.kanban_board_java.dialog.LoadingDialog;
import com.example.kanban_board_java.utils.PreferenceUtils;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

@AndroidEntryPoint
public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    public PreferenceUtils prefsUtils;

    private LoadingDialog dialog;

    private void initDialog() {
        if (!isFinishing()) {
            dialog = new LoadingDialog(this);
            dialog.show();
        }
    }

    public void hideProgress() {
        if (!isFinishing() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showProgress() {
        if (dialog == null) {
            initDialog();
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                initDialog();
            } else {
                initDialog();
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
