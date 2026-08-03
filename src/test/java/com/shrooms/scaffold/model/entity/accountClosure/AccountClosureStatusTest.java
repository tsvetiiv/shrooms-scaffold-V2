package com.shrooms.scaffold.model.entity.accountClosure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountClosureStatusTest {

    @Test
    public void accountClosureStatus_shouldContainExpectedValues() {
        assertEquals(3, AccountClosureStatus.values().length);

        assertEquals(AccountClosureStatus.PENDING, AccountClosureStatus.valueOf("PENDING"));
        assertEquals(AccountClosureStatus.APPROVED, AccountClosureStatus.valueOf("APPROVED"));
        assertEquals(AccountClosureStatus.REJECTED, AccountClosureStatus.valueOf("REJECTED"));
    }
}