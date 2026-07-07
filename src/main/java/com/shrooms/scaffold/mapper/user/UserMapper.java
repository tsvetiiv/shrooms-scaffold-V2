package com.shrooms.scaffold.mapper.user;

import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.dto.user.UserRegisterRequest;
import com.shrooms.scaffold.model.entity.user.RoleType;
import com.shrooms.scaffold.model.entity.user.User;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class UserMapper {

    public static User toUserEntity(UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            return null;
        }

        return User.builder()
                .username(userRegisterRequest.getUsername())
                .password(userRegisterRequest.getPassword())
                .email(userRegisterRequest.getEmail())
                .roleType(RoleType.USER)
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .roleType(user.getRoleType())
                .orders(user.getOrders())
                .build();
    }
}
