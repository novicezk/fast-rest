package com.zhukai.framework.fast.rest.common;

/**
 * Created by homolo on 17-8-2.
 */
public enum HttpStatus {
    OK(200, "OK"),
    BadRequest(400, "Bad Request"),
    NotFound(404, "Not Found"),
    MethodNotAllowed(405, "Method Not Allowed"),
    InternalServerError(500, "Internal Server Error");


    private int code;
    private String codeStr;

    HttpStatus(int code, String codeStr) {
        this.code = code;
        this.codeStr = codeStr;
    }

    public int getCode() {
        return code;
    }

    public String getCodeStr() {
        return codeStr;
    }
}
