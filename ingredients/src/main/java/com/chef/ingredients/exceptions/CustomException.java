package com.chef.ingredients.exceptions;

@SuppressWarnings("serial")
public class CustomException extends RuntimeException {

    private Integer code;

    private String message;

    public CustomException(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
