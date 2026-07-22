package com.shrooms.scaffold.exception.owner;

import com.shrooms.scaffold.exception.ApplicationException;

public class OnlyOwnerCanManageUsersException extends ApplicationException {

    public OnlyOwnerCanManageUsersException() {
        super(
                "Access denied",
                "403",
                "Only the owner can manage users."
        );
    }
}
