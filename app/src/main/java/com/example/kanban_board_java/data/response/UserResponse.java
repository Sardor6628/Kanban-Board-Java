package com.example.kanban_board_java.data.response;

import android.os.Parcelable;

public class UserResponse {

    private String uid;
    private String name;

    public UserResponse(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
