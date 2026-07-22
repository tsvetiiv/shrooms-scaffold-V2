package com.shrooms.scaffold.exception.order;

import com.shrooms.scaffold.exception.ApplicationException;

public class OrderNotFoundException extends ApplicationException {
    public OrderNotFoundException() {
        super(
                "Order not found",
                "404",
                "The requested order could not be found."
        );
    }
}
