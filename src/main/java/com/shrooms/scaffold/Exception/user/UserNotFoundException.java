package com.shrooms.scaffold.Exception.user;

import com.shrooms.scaffold.Exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException() {
        super(
                "User not found",
                "404",
                "The requested user could not be found."
        );
    }
}
