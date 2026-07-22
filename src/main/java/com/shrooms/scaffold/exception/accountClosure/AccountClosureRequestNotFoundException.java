package com.shrooms.scaffold.exception.accountClosure;

import com.shrooms.scaffold.exception.ApplicationException;

public class AccountClosureRequestNotFoundException extends ApplicationException {

    public AccountClosureRequestNotFoundException() {
        super(
                "Account closure request not found",
                "404",
                "The requested account closure request could not be found."
        );
    }
}
