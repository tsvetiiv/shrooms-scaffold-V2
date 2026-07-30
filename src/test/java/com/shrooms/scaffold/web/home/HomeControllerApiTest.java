package com.shrooms.scaffold.web.home;

import com.shrooms.scaffold.web.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
public class HomeControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getHomePage_shouldReturnIndexWhenUserIsAnonymous() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void getHomePage_shouldReturnIndexWhenHomeEndpointIsCalled() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/home");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void getHomePage_shouldRedirectToOwnerWhenUserIsOwner() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/owner"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getHomePage_shouldRedirectToAdminWhenUserIsAdmin() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin"));
    }
}
