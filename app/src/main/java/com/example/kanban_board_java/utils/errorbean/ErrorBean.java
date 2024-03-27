package com.example.kanban_board_java.utils.errorbean;

public class ErrorBean {

    private Throwable throwable;
    private int error;
    private int errorImage;
    private int code;

    public ErrorBean(Throwable throwable, int error, int errorImage, int code) {
        this.throwable = throwable;
        this.error = error;
        this.errorImage = errorImage;
        this.code = code;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getErrorImage() {
        return errorImage;
    }

    public void setErrorImage(int errorImage) {
        this.errorImage = errorImage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
