package com.example.kanban_board_java.ui.completetask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kanban_board_java.R;
import com.example.kanban_board_java.app.AppConstant;
import com.example.kanban_board_java.app.BaseActivity;
import com.example.kanban_board_java.app.KanbanBoardApp;
import com.example.kanban_board_java.data.response.TaskResponse;
import com.example.kanban_board_java.databinding.ActivityCompleteTaskBinding;
import com.example.kanban_board_java.ui.completetask.adapter.CompleteTaskShowAdapter;
import com.example.kanban_board_java.utils.Utils;
import com.example.kanban_board_java.utils.resource.Status;
import com.example.kanban_board_java.viewmodel.TaskViewModel;
import com.opencsv.CSVWriter;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineContextKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

@ExperimentalCoroutinesApi
@AndroidEntryPoint
public class CompleteTaskActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCompleteTaskBinding binding;

    private TaskViewModel viewModel;

    private CompleteTaskShowAdapter completeTaskShowAdapter;

    private ArrayList<TaskResponse> completedTaskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        observer();
        initView();
        setClick();
    }

    private void initView() {
        binding.rvCompleteTask.setLayoutManager(new LinearLayoutManager(this));
        viewModel.getTasksByStatus(AppConstant.COMPLETED_STATUS_VALUE, true);
    }

    private void setClick() {
        binding.btnBack.setOnClickListener(this);
        binding.btnExport.setOnClickListener(this);
    }

    private void observer() {
        viewModel.getCompletedTaskListState().observe(this, it -> {
            switch (it.getStatus()) {
                case SUCCESS:
                    if (completeTaskShowAdapter == null) {
                        completeTaskShowAdapter = new CompleteTaskShowAdapter(this);
                        binding.rvCompleteTask.setAdapter(completeTaskShowAdapter);
                    }
                    if (it.getData() == null) {
                        binding.vaCompleteTask.setDisplayedChild(1);
                    } else {
                        completedTaskList = it.getData();
                        completeTaskShowAdapter.setData(completedTaskList);
                        binding.vaCompleteTask.setDisplayedChild(completedTaskList.isEmpty() ? 1 : 2);
                    }
                    break;
                case LOADING:
                    binding.vaCompleteTask.setDisplayedChild(0);
                    break;
                case ERROR:
                case EXCEPTION:
                    binding.vaCompleteTask.setDisplayedChild(3);
                    break;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBack) {
            getOnBackPressedDispatcher().onBackPressed();
        } else if (v.getId() == R.id.btnExport) {
            if (!completedTaskList.isEmpty()) {
                generateCSV(completedTaskList);
            } else {
                KanbanBoardApp.getAppInstance().showToast("Not found completed task");
            }
        }
    }

    private void generateCSV(ArrayList<TaskResponse> taskList) {
        showProgress();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/completedTask_" + System.currentTimeMillis() + ".csv");
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));

            String[] header = {"Title", "Description", "Created Date", "Created Time", "Completed Date", "Completed Time", "Spent Time"};
            writer.writeNext(header);

            for (TaskResponse data : taskList) {
                String[] data1 = {
                        data.getTitle(),
                        data.getDescription(),
                        Utils.formatString(data.getCreatedTime() / 1000, "yyyy.MM.dd"),
                        Utils.formatString(data.getCreatedTime() / 1000, "HH:mm:ss"),
                        Utils.formatString(data.getCompletedTime() / 1000, "yyyy.MM.dd"),
                        Utils.formatString(data.getCompletedTime() / 1000, "HH:mm:ss"),
                        Utils.formatSecondToString(data.getSpentTime())
                };
                writer.writeNext(data1);
            }
            writer.close();
            runOnUiThread(() -> {
                hideProgress();
                KanbanBoardApp.getAppInstance().showToast("File save in download folder");
            });
        } catch (IOException e) {
            hideProgress();
            KanbanBoardApp.getAppInstance().showToast(getString(R.string.something_went_wrong));
            e.printStackTrace();
        }
    }
}
