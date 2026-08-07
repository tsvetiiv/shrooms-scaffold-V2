package com.shrooms.scaffold.web.owner;

import com.shrooms.scaffold.model.dto.owner.AccountClosureRequestDto;
import com.shrooms.scaffold.model.dto.user.UserManagementDto;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.user.RoleType;
import com.shrooms.scaffold.service.owner.AccountClosureRequestService;
import com.shrooms.scaffold.service.user.UserDetailsData;
import com.shrooms.scaffold.service.user.UserService;
import com.shrooms.scaffold.web.OwnerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
public class OwnerControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AccountClosureRequestService accountClosureRequestService;

    @Test
    @WithMockUser(roles = "OWNER")
    public void getOwnerDashboard_shouldReturnDashboardView() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/owner");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void getUsers_shouldReturnUsersViewWithUsers() throws Exception {

        UserManagementDto user = UserManagementDto.builder()
                .id(UUID.randomUUID())
                .username("ivan")
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan@mail.com")
                .role(RoleType.USER)
                .active(true)
                .blocked(false)
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(user));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/owner/users");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("owner/users"))
                .andExpect(model().attribute("users", List.of(user)));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void getAccountClosures_shouldReturnAccountClosuresViewWithRequests() throws Exception {

        AccountClosureRequestDto accountClosureRequest = AccountClosureRequestDto.builder()
                .id(UUID.randomUUID())
                .username("ivan")
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan@mail.com")
                .requestedOn(LocalDateTime.now())
                .status(AccountClosureStatus.PENDING)
                .build();

        when(accountClosureRequestService.getPendingRequestsForOwner())
                .thenReturn(List.of(accountClosureRequest));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/owner/account-closures");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("owner/account-closures"))
                .andExpect(model().attribute("accountClosureRequests", List.of(accountClosureRequest)));

        verify(accountClosureRequestService).getPendingRequestsForOwner();
    }

    @Test
    public void blockUser_shouldRedirectWithSuccessMessage_whenUserIsBlocked() throws Exception {

        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        UserDetailsData ownerDetails = UserDetailsData.builder()
                .id(ownerId)
                .username("owner")
                .password("123456")
                .roleType(RoleType.OWNER)
                .active(true)
                .blocked(false)
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/owner/users/{id}/block", targetUserId)
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(
                        ownerDetails,
                        null,
                        ownerDetails.getAuthorities()
                )));

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owner/users"))
                .andExpect(flash().attribute("successMessage", "User blocked successfully."));

        verify(userService).blockUser(ownerId, targetUserId);
    }

    @Test
    public void makeAdmin_shouldRedirectWithSuccessMessage_whenUserIsPromoted() throws Exception {

        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        UserDetailsData ownerDetails = UserDetailsData.builder()
                .id(ownerId)
                .username("owner")
                .password("123456")
                .roleType(RoleType.OWNER)
                .active(true)
                .blocked(false)
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/owner/users/{id}/make-admin", targetUserId)
                .with(csrf())
                .with(authentication(new UsernamePasswordAuthenticationToken(
                        ownerDetails,
                        null,
                        ownerDetails.getAuthorities()
                )));

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owner/users"))
                .andExpect(flash().attribute("successMessage", "User promoted to admin successfully."));

        verify(userService).makeAdmin(ownerId, targetUserId);
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void approveAccountClosure_shouldRedirectWithSuccessMessage_whenRequestIsApproved() throws Exception {

        UUID requestId = UUID.randomUUID();

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.post("/owner/account-closures/{id}/approve", requestId)
                        .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owner/account-closures"))
                .andExpect(flash().attribute(
                        "successMessage",
                        "Account closure request approved successfully."
                ));

        verify(accountClosureRequestService).approveRequest(requestId);
    }
}