package com.shrooms.scaffold.integration.user;

import com.shrooms.scaffold.exception.user.RegistrationException;
import com.shrooms.scaffold.exception.user.UserManagementException;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.dto.user.UserRegisterRequest;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.user.UserRepository;
import com.shrooms.scaffold.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    public void register_shouldSaveUserInDatabase_whenRequestIsValid() {

        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("Aleksandar")
                .password("123456")
                .confirmPassword("123456")
                .email("aleksandar@mail.com")
                .firstName("Aleksandar")
                .lastName("Aleksandrov")
                .build();

        long usersBefore = userRepository.count();

        UserDto result = userService.register(request);

        assertEquals(usersBefore + 1, userRepository.count());

        User savedUser = userRepository.findByUsername("Aleksandar")
                .orElseThrow();

        assertEquals("Aleksandar", savedUser.getUsername());
        assertEquals("aleksandar@mail.com", savedUser.getEmail());
        assertEquals("Aleksandar", savedUser.getFirstName());
        assertEquals("Aleksandrov", savedUser.getLastName());

        assertNotEquals("123456", savedUser.getPassword());
        assertTrue(passwordEncoder.matches("123456", savedUser.getPassword()));

        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getEmail(), result.getEmail());
    }

    @Test
    public void register_shouldThrowException_whenUsernameAlreadyExists(){

        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("Aleksandar")
                .password("123456")
                .confirmPassword("123456")
                .email("aleksandar@mail.com")
                .firstName("Aleksandar")
                .lastName("Aleksandrov")
                .build();
        userService.register(request);

        long usersBefore = userRepository.count();

        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(request)
        );

        assertEquals("Username already exists", exception.getMessage());
        assertEquals(usersBefore, userRepository.count());
    }

    @Test
    public void register_shouldThrowException_whenEmailAlreadyExists(){

        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("Aleksandar")
                .password("123456")
                .confirmPassword("123456")
                .email("aleksandar@mail.com")
                .firstName("Aleksandar")
                .lastName("Aleksandrov")
                .build();

        userService.register(request);

        long usersBefore = userRepository.count();

        UserRegisterRequest request2 = UserRegisterRequest.builder()
                .username("Ivan")
                .password("123456")
                .confirmPassword("123456")
                .email("aleksandar@mail.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .build();

        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(request2)
        );

        assertEquals("Email already exists", exception.getMessage());
        assertEquals(usersBefore, userRepository.count());
    }

    @Test
    public void register_shouldThrowException_whenPasswordsDoNotMatch(){

        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("Aleksandar")
                .password("123456")
                .confirmPassword("1234567")
                .email("aleksandar@mail.com")
                .firstName("Aleksandar")
                .lastName("Aleksandrov")
                .build();

        long usersBefore = userRepository.count();

        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(request)
        );

        assertEquals("Passwords don't match", exception.getMessage());
        assertEquals(usersBefore, userRepository.count());
    }
}




