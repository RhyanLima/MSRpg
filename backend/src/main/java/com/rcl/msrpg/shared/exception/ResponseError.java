package com.rcl.msrpg.shared.exception;

import java.util.Date;

import com.rcl.msrpg.system.domain.model.RpgSystem;

public class ResponseError {

    private Date timesTamp = new Date();
    private Integer code = 400;
    private String type;
    private String message;

    public ResponseError(String message) {
        this.message = message;
    }

    public ResponseError(Integer code, String message ) {
        this.message = message;
        this.code = code;
    }

    public ResponseError(Integer code, String type, String message ) {
        this.message = message;
        this.code = code;
        this.type = type;
    }

    public Date timesTamp() {
        return timesTamp;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }

    public String type() {
        return type;
    }

}
