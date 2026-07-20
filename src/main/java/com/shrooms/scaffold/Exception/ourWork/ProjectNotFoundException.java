package com.shrooms.scaffold.Exception.ourWork;

import com.shrooms.scaffold.Exception.ApplicationException;

public class ProjectNotFoundException extends ApplicationException {

    public ProjectNotFoundException() {
        super(
                "Project not found",
                "404",
                "The requested project could not be found."
        );
    }
}
