package com.shrooms.scaffold.event;

import com.shrooms.scaffold.model.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStatusChangedEvent {

    private final String email;
    private final String customerName;
    private final String scaffoldName;
    private final OrderStatus orderStatus;
}
