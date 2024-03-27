package com.example.kanban_board_java.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.kanban_board_java.R;
import com.example.kanban_board_java.databinding.DialogAddTaskBinding;

public class AddTaskDialog extends Dialog {

    public DialogAddTaskBinding binding;

    public AddTaskDialog(Context context) {
        super(context, R.style.Theme_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_task);

        binding = DialogAddTaskBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
