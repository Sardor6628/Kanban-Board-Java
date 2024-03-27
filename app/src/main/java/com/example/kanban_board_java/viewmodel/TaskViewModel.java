package com.example.kanban_board_java.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kanban_board_java.app.AppConstant;
import com.example.kanban_board_java.app.KanbanBoardApp;
import com.example.kanban_board_java.data.response.TaskResponse;
import com.example.kanban_board_java.utils.PreferenceUtils;
import com.example.kanban_board_java.utils.resource.Resource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

@ExperimentalCoroutinesApi
@HiltViewModel
public class TaskViewModel extends ViewModel {

    private final MutableLiveData<Resource<ArrayList<TaskResponse>>> todoTaskListState = new MutableLiveData<>();
    private final MutableLiveData<Resource<ArrayList<TaskResponse>>> progressTaskListState = new MutableLiveData<>();
    private final MutableLiveData<Resource<ArrayList<TaskResponse>>> completedTaskListState = new MutableLiveData<>();

    private final FirebaseFirestore fireStore;
    private final PreferenceUtils preferenceUtils;

    @Inject
    public TaskViewModel(FirebaseFirestore fireStore, PreferenceUtils preferenceUtils) {
        this.fireStore = fireStore;
        this.preferenceUtils = preferenceUtils;
    }

    public LiveData<Resource<ArrayList<TaskResponse>>> getTodoTaskListState() {
        return todoTaskListState;
    }

    public LiveData<Resource<ArrayList<TaskResponse>>> getProgressTaskListState() {
        return progressTaskListState;
    }

    public LiveData<Resource<ArrayList<TaskResponse>>> getCompletedTaskListState() {
        return completedTaskListState;
    }

    public void getTasksByStatus(String taskStatus, boolean isLoading) {
        Thread newThread = new Thread(() -> {
            MutableLiveData<Resource<ArrayList<TaskResponse>>> taskListState;
            switch (taskStatus) {
                case AppConstant.TODO_STATUS_VALUE:
                    taskListState = todoTaskListState;
                    break;
                case AppConstant.PROGRESS_STATUS_VALUE:
                    taskListState = progressTaskListState;
                    break;
                case AppConstant.COMPLETED_STATUS_VALUE:
                    taskListState = completedTaskListState;
                    break;
                default:
                    taskListState = null;
                    break;
            }
            if (isLoading) {
                taskListState.postValue(Resource.loading());
            }
            ArrayList<TaskResponse> taskList = new ArrayList<>();
            fireStore.collection("tasks")
                    .whereEqualTo("userId", preferenceUtils.getUserId())
                    .whereEqualTo("currentStatus", taskStatus)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            TaskResponse taskResponse = setTaskResponse(document.getId(), document.getData());
                            taskList.add(taskResponse);
                        }
                        taskList.sort((task1, task2) -> {
                            switch (taskStatus) {
                                case AppConstant.TODO_STATUS_VALUE:
                                case AppConstant.PROGRESS_STATUS_VALUE:
                                    return Long.compare(task2.getCreatedTime(), task1.getCreatedTime());
                                case AppConstant.COMPLETED_STATUS_VALUE:
                                    return Long.compare(task2.getCompletedTime(), task1.getCompletedTime());
                                default:
                                    return 0;
                            }
                        });
                        if (taskList.isEmpty()) {
                            if (taskListState != null) {
                                taskListState.postValue(Resource.success(null));
                            }
                        } else {
                            if (taskListState != null) {
                                taskListState.postValue(Resource.success(taskList));
                            }
                        }
                    })
                    .addOnFailureListener(exception -> {
                        if (taskListState != null) {
                            taskListState.postValue(Resource.error(exception));
                        }
                    });
        });
        newThread.start();
    }

    public void updateTodoTask(TaskResponse taskResponse, int type) {
        Thread newThread = new Thread(() -> {
            MutableLiveData<Resource<ArrayList<TaskResponse>>> taskListState = null;
            switch (taskResponse.getCurrentStatus()) {
                case AppConstant.TODO_STATUS_VALUE:
                    taskListState = todoTaskListState;
                    break;
                case AppConstant.PROGRESS_STATUS_VALUE:
                    taskListState = progressTaskListState;
                    break;
                case AppConstant.COMPLETED_STATUS_VALUE:
                    taskListState = completedTaskListState;
                    break;
            }

            if (taskListState != null && taskListState.getValue() != null && taskListState.getValue().getData() != null) {
                ArrayList<TaskResponse> list = taskListState.getValue().getData();
                if (type == -1) {
                    int pos = -1;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId().equals(taskResponse.getId())) {
                            pos = i;
                            break;
                        }
                    }
                    if (pos != -1) {
                        list.set(pos, taskResponse);
                    } else {
                        getTasksByStatus(AppConstant.TODO_STATUS_VALUE, true);
                    }
                } else {
                    list.add(taskResponse);
                }
                list.sort((t1, t2) -> {
                    switch (taskResponse.getCurrentStatus()) {
                        case AppConstant.TODO_STATUS_VALUE:
                        case AppConstant.PROGRESS_STATUS_VALUE:
                            return Long.compare(t2.getCreatedTime(), t1.getCreatedTime());
                        case AppConstant.COMPLETED_STATUS_VALUE:
                            return Long.compare(t2.getCompletedTime(), t1.getCompletedTime());
                        default:
                            return 0;
                    }
                });
                taskListState.postValue(Resource.success(list));
            } else {
                ArrayList<TaskResponse> newList = new ArrayList<>();
                newList.add(taskResponse);
                if (taskListState != null) {
                    taskListState.postValue(Resource.success(newList));
                }
            }

            fireStore.collection("tasks").document(taskResponse.getDocumentId()).set(setTaskResponseToFirebase(taskResponse))
                    .addOnCompleteListener(task -> KanbanBoardApp.getAppInstance().showToast("Task Updated"))
                    .addOnFailureListener(e -> KanbanBoardApp.getAppInstance().showToast(e.getMessage()));
        });
        newThread.start();
    }

    public void deleteTodoTask(TaskResponse taskResponse) {
        Thread newThread = new Thread(() -> {
            MutableLiveData<Resource<ArrayList<TaskResponse>>> taskListState = null;
            switch (taskResponse.getCurrentStatus()) {
                case AppConstant.TODO_STATUS_VALUE:
                    taskListState = todoTaskListState;
                    break;
                case AppConstant.PROGRESS_STATUS_VALUE:
                    taskListState = progressTaskListState;
                    break;
                case AppConstant.COMPLETED_STATUS_VALUE:
                    taskListState = completedTaskListState;
                    break;
            }

            if (taskListState != null && taskListState.getValue() != null && taskListState.getValue().getData() != null) {
                ArrayList<TaskResponse> list = taskListState.getValue().getData();
                int pos = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equals(taskResponse.getId())) {
                        pos = i;
                        break;
                    }
                }
                if (pos != -1) {
                    list.remove(pos);
                    taskListState.postValue(Resource.success(list));
                } else {
                    KanbanBoardApp.getAppInstance().showToast("Task not deleted");
                }
            }

            fireStore.collection("tasks").document(taskResponse.getDocumentId()).delete()
                    .addOnCompleteListener(task -> KanbanBoardApp.getAppInstance().showToast("Task Deleted"))
                    .addOnFailureListener(e -> KanbanBoardApp.getAppInstance().showToast(e.getMessage()));
        });
        newThread.start();
    }


    public void addTodoTask(TaskResponse taskResponse) {
        Thread newThread = new Thread(() -> {
            fireStore.collection("tasks")
                    .add(setTaskResponseToFirebase(taskResponse))
                    .addOnCompleteListener(task -> {
                        taskResponse.setDocumentId(Objects.requireNonNull(task.getResult()).getId());
                        if (todoTaskListState.getValue() != null && todoTaskListState.getValue().getData() != null) {
                            ArrayList<TaskResponse> list = new ArrayList<>(todoTaskListState.getValue().getData());
                            list.add(taskResponse);
                            list.sort((task1, task2) -> Long.compare(task2.getCreatedTime(), task1.getCreatedTime()));
                            todoTaskListState.postValue(Resource.success(list));
                        } else {
                            ArrayList<TaskResponse> newList = new ArrayList<>();
                            newList.add(taskResponse);
                            todoTaskListState.postValue(Resource.success(newList));
                        }
                        KanbanBoardApp.getAppInstance().showToast("Task Added");
                    });

        });
        newThread.start();
    }

    private TaskResponse setTaskResponse(String documentId, Map<String, Object> data) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setDocumentId(documentId);

        if (data != null) {
            taskResponse.setId(String.valueOf(data.get("id")));
            taskResponse.setTitle(String.valueOf(data.get("title")));
            taskResponse.setDescription(String.valueOf(data.get("description")));
            taskResponse.setCurrentStatus(String.valueOf(data.get("currentStatus")));
            taskResponse.setUserId(String.valueOf(data.get("userId")));
            taskResponse.setStartedTime((Long) data.getOrDefault("startedTime", null));
            taskResponse.setCreatedTime((Long) data.getOrDefault("createdTime", null));
            taskResponse.setCompletedTime((Long) data.getOrDefault("completedTime", null));
            taskResponse.setSpentTime((Long) data.getOrDefault("spentTime", 0L));
        }

        return taskResponse;
    }

    private Map<String, Object> setTaskResponseToFirebase(TaskResponse taskResponse) {
        Map<String, Object> firebaseData = new HashMap<>();
        firebaseData.put("id", taskResponse.getId());
        firebaseData.put("title", taskResponse.getTitle());
        firebaseData.put("userId", taskResponse.getUserId());
        firebaseData.put("description", taskResponse.getDescription());
        firebaseData.put("createdTime", taskResponse.getCreatedTime());
        firebaseData.put("completedTime", taskResponse.getCompletedTime());
        firebaseData.put("startedTime", taskResponse.getStartedTime());
        firebaseData.put("spentTime", taskResponse.getSpentTime());
        firebaseData.put("currentStatus", taskResponse.getCurrentStatus());
        return firebaseData;
    }

}