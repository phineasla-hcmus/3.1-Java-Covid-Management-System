package com.seasidechachacha.common.payment;

import java.io.Serializable;

public class ErrorResponse implements Serializable {
    ErrorResponseType type;
    String message;

    public ErrorResponse(ErrorResponseType type) {
        this.type = type;
    }

    public ErrorResponse(ErrorResponseType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ErrorResponseType getType() {
        return type;
    }

    public void setType(ErrorResponseType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
