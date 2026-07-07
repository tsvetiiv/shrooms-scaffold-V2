package com.shrooms.scaffold.event.accountClosure;

import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountClosureStatusChangedEvent {

    private String username;
    private String email;
    private AccountClosureStatus closureStatus;
    private boolean active;
}
