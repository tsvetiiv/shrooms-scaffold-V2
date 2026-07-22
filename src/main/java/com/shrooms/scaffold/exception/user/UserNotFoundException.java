package com.shrooms.scaffold.exception.user;

import com.shrooms.scaffold.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException() {
        super(
                "User not found",
                "404",
                "The requested user could not be found."
        );
    }
}
