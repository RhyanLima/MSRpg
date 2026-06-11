package com.rcl.msrpg.shared.exception;

import java.util.Date;

public class ResponseError {

    private Date timesTamp = new Date();
    private Integer statusCode = 400;
    private String code;
    private String message;

    public ResponseError(String message) {
        this.message = message;
    }

    public ResponseError(Integer statusCode, String message ) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public ResponseError(Integer statusCode, String code, String message ) {
        this.message = message;
        this.statusCode = statusCode;
        this.code = code;
    }

    public Date timesTamp() {
        return timesTamp;
    }

    public Integer statusCode() {
        return statusCode;
    }

    public String message() {
        return message;
    }

    public String code() {
        return code;
    }

}
