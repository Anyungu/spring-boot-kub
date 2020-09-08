package com.chef.ingredients.responses;

import java.time.LocalDateTime;

public class StatusResponse {

    private LocalDateTime time;
    private String message;
    private Integer code;

    public StatusResponse() {
        super();
    }

    public StatusResponse(LocalDateTime time, String message, Integer code) {
        super();
        this.time = time;
        this.message = message;
        this.code = code;
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

    @Override
    public String toString() {
        return "StatusResponse [time=" + time + ", message=" + message + ", code=" + code + "]";
    }
}