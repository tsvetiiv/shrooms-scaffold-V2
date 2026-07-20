package com.shrooms.scaffold.Exception.owner;

import com.shrooms.scaffold.Exception.ApplicationException;

public class OwnerAccountCannotBeModifyException extends ApplicationException {

    public OwnerAccountCannotBeModifyException() {
        super(
                "Access denied",
                "403",
                "Owner accounts cannot be modified."
        );
    }
}
