package com.shrooms.scaffold.web.home;

import com.shrooms.scaffold.web.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HomeControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getHomePage_shouldReturnIndexWhenUserIsAnonymous() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void getHomePage_shouldReturnIndexWhenHomeEndpointIsCalled() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/home");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void getHomePage_shouldRedirectToOwnerWhenUserIsOwner() throws Exception {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "owner",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
                );

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owner"));
    }

    @Test
    public void getHomePage_shouldRedirectToAdminWhenUserIsAdmin() throws Exception {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }
    @Test
    public void getHomePage_shouldReturnIndexWhenUserHasUserRole() throws Exception {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
