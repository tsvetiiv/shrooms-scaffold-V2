package com.shrooms.scaffold.Exception.owner;

import com.shrooms.scaffold.Exception.ApplicationException;

public class OwnerNotFoundException extends ApplicationException {

    public OwnerNotFoundException() {
        super(
                "Owner not found",
                "404",
                "The requested owner could not be found."
        );
    }
}
