package com.chef.ingredients.responses;

import java.time.LocalDateTime;

public class GeneralResponse<T> {

    private LocalDateTime time;
    private String message;
    private Integer code;
    private T data;

    public GeneralResponse() {
        super();
    }

    public GeneralResponse(LocalDateTime time, String message, Integer code, T data) {
        super();
        this.time = time;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GeneralResponse [time=" + time + ", message=" + message + ", code=" + code + ", data=" + data + "]";
    }

}