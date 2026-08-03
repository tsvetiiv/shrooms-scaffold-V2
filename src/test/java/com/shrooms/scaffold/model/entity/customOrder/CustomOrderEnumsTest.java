package com.shrooms.scaffold.model.entity.customOrder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomOrderEnumsTest {

    @Test
    public void requestStatus_shouldContainExpectedValues() {
        assertEquals(3, RequestStatus.values().length);

        assertEquals(RequestStatus.PENDING, RequestStatus.valueOf("PENDING"));
        assertEquals(RequestStatus.APPROVED, RequestStatus.valueOf("APPROVED"));
        assertEquals(RequestStatus.REJECTED, RequestStatus.valueOf("REJECTED"));
    }
}