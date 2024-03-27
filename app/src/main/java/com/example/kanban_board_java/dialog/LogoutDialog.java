package com.example.kanban_board_java.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.example.kanban_board_java.R;
import com.example.kanban_board_java.databinding.DialogLogoutBinding;

public class LogoutDialog extends Dialog {

    public DialogLogoutBinding binding;
    private String email;

    public LogoutDialog(Context context, String email) {
        super(context, R.style.Theme_Dialog);
        this.email = email;
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogLogoutBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        binding.tvNotice.setText(email + " are you sure you want to logout?");

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

