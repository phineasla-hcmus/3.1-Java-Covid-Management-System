package com.seasidechachacha.client.payment;

import com.seasidechachacha.common.payment.ErrorResponse;
import com.seasidechachacha.common.payment.ErrorResponseType;

public class RespondException extends Exception {
    private final ErrorResponseType type;

    public RespondException(ErrorResponse error) {
        super(error.getMessage());
        type = error.getType();
    }

    public RespondException(ErrorResponse error, Throwable cause) {
        super(error.getMessage(), cause);
        type = error.getType();
    }

    public ErrorResponseType getType() {
        return type;
    }
}
