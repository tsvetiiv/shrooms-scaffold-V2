package com.shrooms.scaffold.model.entity.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderEnumsTest {

    @Test
    public void orderStatus_shouldContainExpectedValues() {
        assertEquals(3, OrderStatus.values().length);

        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.APPROVED, OrderStatus.valueOf("APPROVED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }

    @Test
    public void orderType_shouldContainExpectedValues() {
        assertEquals(3, OrderType.values().length);

        assertEquals(OrderType.RENT, OrderType.valueOf("RENT"));
        assertEquals(OrderType.PURCHASE, OrderType.valueOf("PURCHASE"));
        assertEquals(OrderType.CUSTOM, OrderType.valueOf("CUSTOM"));
    }
}