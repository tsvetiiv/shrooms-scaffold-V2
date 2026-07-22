package com.shrooms.scaffold.exception.owner;

import com.shrooms.scaffold.exception.ApplicationException;

public class OwnerNotFoundException extends ApplicationException {

    public OwnerNotFoundException() {
        super(
                "Owner not found",
                "404",
                "The requested owner could not be found."
        );
    }
}
