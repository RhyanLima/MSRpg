package com.rcl.msrpg.shared.exception;

import java.util.Date;

public class ResponseError {

    private Date timesTamp = new Date();
    private String status = "error";
    private Integer statusCode = 400;
    private String message;

    public ResponseError(String message) {
        this.message = message;
    }

    public ResponseError(Integer statusCode, String message ) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public ResponseError(String status, String message) {
        this.message = message;
        this.status = status;
    }

    public ResponseError(Integer statusCode, String status, String message) {
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
    }

    public Date timesTamp() {
        return timesTamp;
    }

    public String status() {
        return status;
    }

    public Integer statusCode() {
        return statusCode;
    }

    public String message() {
        return message;
    }

}
