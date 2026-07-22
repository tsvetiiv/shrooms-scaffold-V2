package com.shrooms.scaffold.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final String errorTitle;
    private final String errorCode;

    public ApplicationException(
            String errorTitle,
            String errorCode,
            String message) {

        super(message);
        this.errorTitle = errorTitle;
        this.errorCode = errorCode;
    }
}
