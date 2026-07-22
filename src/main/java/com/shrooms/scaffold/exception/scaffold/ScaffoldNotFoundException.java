package com.shrooms.scaffold.exception.scaffold;

import com.shrooms.scaffold.exception.ApplicationException;

public class ScaffoldNotFoundException extends ApplicationException {

    public ScaffoldNotFoundException() {
        super(
                "Scaffold not found",
                "404",
                "The requested scaffold could not be found."
        );
    }
}
