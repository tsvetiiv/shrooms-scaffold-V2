package com.shrooms.scaffold.event.role;

import com.shrooms.scaffold.model.entity.user.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleChangedEvent {

    private String username;
    private RoleType roleType;
    private String email;
}
