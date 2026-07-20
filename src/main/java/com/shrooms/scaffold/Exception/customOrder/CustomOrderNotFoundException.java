package com.shrooms.scaffold.Exception.customOrder;

import com.shrooms.scaffold.Exception.ApplicationException;

public class CustomOrderNotFoundException extends ApplicationException {

    public CustomOrderNotFoundException() {
        super(
                "Custom order not found.",
                "404",
                "The requested custom order could not be found."
        );
    }
}
