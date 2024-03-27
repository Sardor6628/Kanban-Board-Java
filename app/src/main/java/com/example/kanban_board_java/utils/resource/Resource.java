package com.example.kanban_board_java.utils.resource;

import com.example.kanban_board_java.utils.errorbean.ErrorBean;

public class Resource<T> {
    private final Status status;
    private final T data;
    private final Object error;
    private final ErrorBean errorBean;

    public Resource(Status status, T data, Object error, ErrorBean errorBean) {
        this.status = status;
        this.data = data;
        this.error = error;
        this.errorBean = errorBean;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Resource<T> success(T data, Integer systemApp) {
        return new Resource<>(Status.SUCCESS, data, systemApp, null);
    }

    public static <T> Resource<T> error(Object error) {
        return new Resource<>(Status.ERROR, null, error, null);
    }

    public static <T> Resource<T> exception(Throwable error) {
        return new Resource<>(Status.EXCEPTION, null, error, null);
    }

    public static <T> Resource<T> exceptionBean(ErrorBean errorBean) {
        return new Resource<>(Status.EXCEPTION, null, null, errorBean);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null, null);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public Object getError() {
        return error;
    }

    public ErrorBean getErrorBean() {
        return errorBean;
    }
}
