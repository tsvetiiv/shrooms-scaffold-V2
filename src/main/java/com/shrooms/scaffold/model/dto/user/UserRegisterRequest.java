package com.shrooms.scaffold.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 6, message = "Username must be 6 or more characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be 6 or more characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    @Size(min = 6, message = "Confirm password must be at least 6 characters")
    private String confirmPassword;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email must contain a valid domain")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;


}
