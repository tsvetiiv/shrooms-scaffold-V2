package com.shrooms.scaffold.Exception.scaffold;

import com.shrooms.scaffold.Exception.ApplicationException;

public class ScaffoldNotFoundException extends ApplicationException {

    public ScaffoldNotFoundException() {
        super(
                "Scaffold not found",
                "404",
                "The requested scaffold could not be found."
        );
    }
}
