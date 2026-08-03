package com.shrooms.scaffold.web.user;

import com.shrooms.scaffold.exception.user.RegistrationException;
import com.shrooms.scaffold.model.dto.user.UserRegisterRequest;
import com.shrooms.scaffold.service.user.UserService;
import com.shrooms.scaffold.web.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getRegisterPage_shouldReturnRegisterViewAndModelAttribute() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/register");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userRegisterRequest"));
    }

    @Test
    public void register_shouldRedirectToSuccessPageWhenRegistrationIsSuccessful() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/register")
                .formField("username", "Ivancho")
                .formField("password", "123456")
                .formField("confirmPassword", "123456")
                .formField("email", "ivan@mail.com")
                .formField("firstName", "Ivan")
                .formField("lastName", "Ivanov")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/success"));

        verify(userService).register(any(UserRegisterRequest.class));
    }

    @Test
    public void register_shouldReturnRegisterView_whenRequestIsInvalid() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/register")
                .formField("username", "Ivan")
                .formField("password", "1234")
                .formField("confirmPassword", "1234")
                .formField("email", "ivan@mail.com")
                .formField("firstName", "Ivan")
                .formField("lastName", "Ivanov")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userRegisterRequest"));

        verify(userService, never()).register(any(UserRegisterRequest.class));
    }
    @Test
    public void getRegisterSuccessPage_shouldReturnRegisterSuccessView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register/success"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-success"));
    }

    @Test
    public void register_shouldReturnRegisterViewWhenRegistrationExceptionIsThrown() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/register")
                .formField("username", "Ivancho")
                .formField("password", "123456")
                .formField("confirmPassword", "123456")
                .formField("email", "ivan@mail.com")
                .formField("firstName", "Ivan")
                .formField("lastName", "Ivanov")
                .with(csrf());

        doThrow(new RegistrationException("email", "Email already exists"))
                .when(userService)
                .register(any(UserRegisterRequest.class));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("register"));

        verify(userService).register(any(UserRegisterRequest.class));
    }
}
