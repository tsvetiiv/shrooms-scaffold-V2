package com.shrooms.scaffold.Exception.order;

import com.shrooms.scaffold.Exception.ApplicationException;

public class OrderNotFoundException extends ApplicationException {
    public OrderNotFoundException() {
        super(
                "Order not found",
                "404",
                "The requested order could not be found."
        );
    }
}
