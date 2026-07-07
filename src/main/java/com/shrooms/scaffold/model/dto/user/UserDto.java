package com.shrooms.scaffold.model.dto.user;

import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.entity.user.RoleType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String email;
    private RoleType roleType;
    private List<Order> orders;
    private boolean active;
    private boolean blocked;
}
