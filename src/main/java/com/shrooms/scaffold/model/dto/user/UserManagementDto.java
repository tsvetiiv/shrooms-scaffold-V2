package com.shrooms.scaffold.model.dto.user;

import com.shrooms.scaffold.model.entity.user.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private RoleType role;
    private boolean active;
    private boolean blocked;
}
