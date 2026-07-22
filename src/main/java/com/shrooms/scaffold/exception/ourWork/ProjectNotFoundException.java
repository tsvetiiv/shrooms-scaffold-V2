package com.shrooms.scaffold.exception.ourWork;

import com.shrooms.scaffold.exception.ApplicationException;

public class ProjectNotFoundException extends ApplicationException {

    public ProjectNotFoundException() {
        super(
                "Project not found",
                "404",
                "The requested project could not be found."
        );
    }
}
