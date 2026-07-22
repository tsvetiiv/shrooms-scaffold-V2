package com.shrooms.scaffold.exception.owner;

import com.shrooms.scaffold.exception.ApplicationException;

public class OwnerAccountCannotBeModifyException extends ApplicationException {

    public OwnerAccountCannotBeModifyException() {
        super(
                "Access denied",
                "403",
                "Owner accounts cannot be modified."
        );
    }
}
