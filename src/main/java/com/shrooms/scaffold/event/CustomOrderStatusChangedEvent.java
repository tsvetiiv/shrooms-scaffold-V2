package com.shrooms.scaffold.event;

import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomOrderStatusChangedEvent {

    private final String email;
    private final String customerName;
    private final String projectName;
    private final RequestStatus requestStatus;
}
