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

    public Date getTimesTamp() {
        return timesTamp;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

}
