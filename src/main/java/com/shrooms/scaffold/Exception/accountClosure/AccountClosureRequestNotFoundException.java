package com.shrooms.scaffold.Exception.accountClosure;

import com.shrooms.scaffold.Exception.ApplicationException;

public class AccountClosureRequestNotFoundException extends ApplicationException {

    public AccountClosureRequestNotFoundException() {
        super(
                "Account closure request not found",
                "404",
                "The requested account closure request could not be found."
        );
    }
}
