package com.shrooms.scaffold.service.user;

import com.shrooms.scaffold.mapper.user.UserMapper;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.dto.user.UserEditProfileDto;
import com.shrooms.scaffold.model.dto.user.UserLoginRequest;
import com.shrooms.scaffold.model.dto.user.UserRegisterRequest;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.findByUsername(userRegisterRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (!userRegisterRequest.getPassword()
                .equals(userRegisterRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords don't match");
        }
        User user = UserMapper.toUserEntity(userRegisterRequest);
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));

        User savedUser = userRepository.save(user);

        return UserMapper.toUserDto(savedUser);

    }

    public UserDto login(UserLoginRequest userLoginRequest) {
        Optional<User> optionalUser =
                userRepository.findByUsername(userLoginRequest.getUsername());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Username not found");
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return UserMapper.toUserDto(user);
    }

    public UserDto editProfile(UUID userId, UserEditProfileDto userEditProfileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(userEditProfileDto.getEmail())
                && userRepository.existsByEmail(userEditProfileDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setFirstName(userEditProfileDto.getFirstName());
        user.setLastName(userEditProfileDto.getLastName());
        user.setEmail(userEditProfileDto.getEmail());
        user.setProfilePicture(userEditProfileDto.getProfilePicture());

        User savedUser = userRepository.save(user);

        return UserMapper.toUserDto(savedUser);
    }
}
