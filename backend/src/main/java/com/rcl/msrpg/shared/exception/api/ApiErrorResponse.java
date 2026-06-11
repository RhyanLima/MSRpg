package com.rcl.msrpg.shared.exception.api;

import com.rcl.msrpg.shared.exception.ResponseError;

public class ApiErrorResponse extends ResponseError {

    String path;
    String requestId;

    private ApiErrorResponse(Integer statusCode, String code, String message, String path, String requestId) {
        super(statusCode, code, message);
        this.path = path;
        this.requestId = requestId;
    }

    public static ApiErrorResponse of(Integer statusCode, String code, String message, String path, String requestId) {
        return new ApiErrorResponse(statusCode, code, message, path, requestId);
    }

}
