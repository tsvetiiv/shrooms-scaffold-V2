package com.shrooms.scaffold.Exception.owner;

import com.shrooms.scaffold.Exception.ApplicationException;

public class OnlyOwnerCanManageUsersException extends ApplicationException {

    public OnlyOwnerCanManageUsersException() {
        super(
                "Access denied",
                "403",
                "Only the owner can manage users."
        );
    }
}
