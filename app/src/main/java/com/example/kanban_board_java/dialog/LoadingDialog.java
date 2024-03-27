package com.example.kanban_board_java.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import androidx.appcompat.content.res.AppCompatResources;
import com.example.kanban_board_java.R;
import com.example.kanban_board_java.databinding.DialogLoadingBinding;

public class LoadingDialog extends Dialog {

    private DialogLoadingBinding binding;

    public LoadingDialog(Context context) {
        super(context, R.style.Theme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(context, R.color.transparent));
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogLoadingBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());
    }
}

