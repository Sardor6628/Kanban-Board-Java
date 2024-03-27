package com.example.kanban_board_java.ui.home;

import static com.example.kanban_board_java.utils.resource.Status.*;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kanban_board_java.R;
import com.example.kanban_board_java.app.AppConstant;
import com.example.kanban_board_java.app.BaseActivity;
import com.example.kanban_board_java.app.KanbanBoardApp;
import com.example.kanban_board_java.data.response.TaskResponse;
import com.example.kanban_board_java.databinding.ActivityHomeBinding;
import com.example.kanban_board_java.dialog.AddTaskDialog;
import com.example.kanban_board_java.dialog.LogoutDialog;
import com.example.kanban_board_java.ui.completetask.CompleteTaskActivity;
import com.example.kanban_board_java.ui.home.adapter.CompleteTaskAdapter;
import com.example.kanban_board_java.ui.home.adapter.ProgressTaskAdapter;
import com.example.kanban_board_java.ui.home.adapter.TodoTaskAdapter;
import com.example.kanban_board_java.ui.login.LoginActivity;
import com.example.kanban_board_java.utils.resource.Resource;
import com.example.kanban_board_java.viewmodel.TaskViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

@ExperimentalCoroutinesApi
@AndroidEntryPoint
public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityHomeBinding binding;

    private TaskViewModel viewModel;

    private TodoTaskAdapter todoTaskAdapter;
    private ProgressTaskAdapter progressTaskAdapter;
    private CompleteTaskAdapter completedTaskAdapter;

    private ArrayList<TaskResponse> todoTaskList = new ArrayList<>();
    private ArrayList<TaskResponse> progressTaskList = new ArrayList<>();
    private ArrayList<TaskResponse> completedTaskList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        observe();
        initView();
        setClick();
    }

    private void initView() {
        binding.rvTodoTask.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvProgressTask.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCompleteTask.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        viewModel.getTasksByStatus(AppConstant.TODO_STATUS_VALUE, true);
        viewModel.getTasksByStatus(AppConstant.PROGRESS_STATUS_VALUE, true);
        viewModel.getTasksByStatus(AppConstant.COMPLETED_STATUS_VALUE, true);
    }

    private void setClick() {
        binding.btnAddTask.setOnClickListener(this);
        binding.btnAddNewTask.setOnClickListener(this);
        binding.btnViewAll.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);
    }

    private void observe() {
        viewModel.getTodoTaskListState().observe(this, it -> {
            switch (it.getStatus()) {
                case SUCCESS:
                    if (todoTaskAdapter == null) {
                        todoTaskAdapter = new TodoTaskAdapter(HomeActivity.this);
                        binding.rvTodoTask.setAdapter(todoTaskAdapter);

                        todoTaskAdapter.setOnItemClick((data, pos) -> {
                            data.setCurrentStatus(AppConstant.PROGRESS_STATUS_VALUE);
                            data.setStartedTime(System.currentTimeMillis() * 1000);
                            viewModel.updateTodoTask(data, 0);
                            todoTaskList.remove(pos);
                            todoTaskAdapter.setData(todoTaskList);
                            binding.vaTodoTask.setDisplayedChild(todoTaskList.isEmpty() ? 1 : 2);
                        });

                        todoTaskAdapter.setOnItemMenu((data, id) -> menuClick(id, data));
                    }

                    if (it.getData() == null) {
                        binding.vaTodoTask.setDisplayedChild(1);
                    } else {
                        todoTaskList = it.getData();
                        todoTaskAdapter.setData(todoTaskList);
                        binding.vaTodoTask.setDisplayedChild(todoTaskList.isEmpty() ? 1 : 2);
                    }
                    break;
                case LOADING:
                    binding.vaTodoTask.setDisplayedChild(0);
                    break;
                case ERROR:
                case EXCEPTION:
                    binding.vaTodoTask.setDisplayedChild(3);
                    break;
            }
        });

        viewModel.getProgressTaskListState().observe(this, resource -> {
            switch (resource.getStatus()) {
                case SUCCESS:
                    if (progressTaskAdapter == null) {
                        progressTaskAdapter = new ProgressTaskAdapter(this);
                        binding.rvProgressTask.setAdapter(progressTaskAdapter);

                        progressTaskAdapter.setOnItemClickComplete((data, pos) -> {
                            data.setCurrentStatus(AppConstant.COMPLETED_STATUS_VALUE);
                            data.setCompletedTime(System.currentTimeMillis() * 1000);
                            viewModel.updateTodoTask(data, 0);
                            progressTaskList.remove(pos);
                            progressTaskAdapter.setData(progressTaskList);
                            binding.vaProgressTask.setDisplayedChild(progressTaskList.isEmpty() ? 1 : 2);
                        });

                        progressTaskAdapter.setOnItemClickPause((data, pos) -> {
                            data.setCurrentStatus(AppConstant.TODO_STATUS_VALUE);
                            viewModel.updateTodoTask(data, 0);
                            progressTaskList.remove(pos);
                            progressTaskAdapter.setData(progressTaskList);
                            binding.vaProgressTask.setDisplayedChild(progressTaskList.isEmpty() ? 1 : 2);
                        });
                    }

                    if (resource.getData() == null) {
                        binding.vaProgressTask.setDisplayedChild(1);
                    } else {
                        progressTaskList = resource.getData();
                        progressTaskAdapter.setData(progressTaskList);
                        binding.vaProgressTask.setDisplayedChild(progressTaskList.isEmpty() ? 1 : 2);
                    }
                    break;
                case LOADING:
                    binding.vaProgressTask.setDisplayedChild(0);
                    break;
                case ERROR:
                case EXCEPTION:
                    binding.vaProgressTask.setDisplayedChild(3);
                    break;
            }
        });

        viewModel.getCompletedTaskListState().observe(this, resource -> {
            switch (resource.getStatus()) {
                case SUCCESS:
                    if (completedTaskAdapter == null) {
                        completedTaskAdapter = new CompleteTaskAdapter(this);
                        binding.rvCompleteTask.setAdapter(completedTaskAdapter);
                        completedTaskAdapter.setOnItemMenuClickListener((data, id) -> menuClick(id, data));
                    }

                    if (resource.getData() == null) {
                        binding.vaCompleteTask.setDisplayedChild(1);
                    } else {
                        completedTaskList = resource.getData();
                        completedTaskAdapter.setData(completedTaskList);
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

    private void menuClick(int id, TaskResponse data) {
        if (id == R.id.edit) {
            AddTaskDialog addTaskDialog = new AddTaskDialog(this);
            addTaskDialog.show();

            addTaskDialog.binding.tvTitle.setText(getString(R.string.update));
            addTaskDialog.binding.tvAdd.setText(getString(R.string.update));

            addTaskDialog.binding.etTitle.setText(data.getTitle());
            addTaskDialog.binding.etDescription.setText(data.getDescription());

            addTaskDialog.binding.btnAdd.setOnClickListener(view -> {
                if (!addTaskDialog.binding.etTitle.getText().toString().isEmpty()) {
                    if (!addTaskDialog.binding.etDescription.getText().toString().isEmpty()) {
                        data.setTitle(addTaskDialog.binding.etTitle.getText().toString());
                        data.setDescription(addTaskDialog.binding.etDescription.getText().toString());
                        data.setCreatedTime(System.currentTimeMillis()*1000);
                        addTaskDialog.dismiss();
                        viewModel.updateTodoTask(data, -1);
                    } else {
                        KanbanBoardApp.getAppInstance().showToast("Please enter description");
                    }
                } else {
                    KanbanBoardApp.getAppInstance().showToast("Please enter title");
                }
            });
        } else if (id == R.id.delete) {
            viewModel.deleteTodoTask(data);
        } else if (id == R.id.reset) {
            data.setSpentTime(0L);
            viewModel.updateTodoTask(data, -1);
        }

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnAddNewTask || viewId == R.id.btnAddTask) {
            AddTaskDialog addTaskDialog = new AddTaskDialog(this);
            addTaskDialog.show();

            addTaskDialog.binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!addTaskDialog.binding.etTitle.getText().toString().isEmpty()) {
                        if (!addTaskDialog.binding.etDescription.getText().toString().isEmpty()) {
                            TaskResponse taskResponse = new TaskResponse();
                            taskResponse.setId(String.valueOf(System.currentTimeMillis()));
                            taskResponse.setTitle(addTaskDialog.binding.etTitle.getText().toString());
                            taskResponse.setDescription(addTaskDialog.binding.etDescription.getText().toString());
                            taskResponse.setUserId(prefsUtils.getUserId());
                            taskResponse.setCreatedTime(System.currentTimeMillis() * 1000);
                            taskResponse.setCurrentStatus(AppConstant.TODO_STATUS_VALUE);
                            viewModel.addTodoTask(taskResponse);
                            addTaskDialog.dismiss();
                        } else {
                            KanbanBoardApp.getAppInstance().showToast("Please enter description");
                        }
                    } else {
                        KanbanBoardApp.getAppInstance().showToast("Please enter title");
                    }
                }
            });
        } else if (viewId == R.id.btnViewAll) {
            startActivity(new Intent(this, CompleteTaskActivity.class));
        } else if (viewId == R.id.btnLogout) {
            LogoutDialog logOutDialog = new LogoutDialog(this, FirebaseAuth.getInstance().getCurrentUser().getEmail());
            logOutDialog.show();
            logOutDialog.binding.btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logOutDialog.dismiss();
                    logout();
                }
            });
        }
    }

    private void logout() {
        showProgress();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            prefsUtils.setLogin(false);
            prefsUtils.setUserId("");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        hideProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getTasksByStatus(AppConstant.TODO_STATUS_VALUE, false);
        viewModel.getTasksByStatus(AppConstant.PROGRESS_STATUS_VALUE, false);
        viewModel.getTasksByStatus(AppConstant.COMPLETED_STATUS_VALUE, false);
    }
}
