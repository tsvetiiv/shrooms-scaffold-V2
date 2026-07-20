package com.shrooms.scaffold.web;

import com.shrooms.scaffold.Exception.accountClosure.AccountClosureException;
import com.shrooms.scaffold.Exception.user.UserManagementException;
import com.shrooms.scaffold.model.dto.owner.AccountClosureRequestDto;
import com.shrooms.scaffold.model.dto.user.UserManagementDto;
import com.shrooms.scaffold.service.owner.AccountClosureRequestService;
import com.shrooms.scaffold.service.user.UserDetailsData;
import com.shrooms.scaffold.service.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    private final UserService userService;
    private final AccountClosureRequestService accountClosureRequestService;

    public OwnerController(UserService userService, AccountClosureRequestService accountClosureRequestService) {
        this.userService = userService;
        this.accountClosureRequestService = accountClosureRequestService;
    }


    @GetMapping
    public ModelAndView getOwnerDashboard() {
        return new ModelAndView("admin/dashboard");
    }

    @GetMapping("/users")
    public ModelAndView getUsers() {
        List<UserManagementDto> allUsers = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView("owner/users");
        modelAndView.addObject("users", allUsers);
        return modelAndView;
    }

    @PostMapping("/users/{id}/block")
    public ModelAndView blockUser(@AuthenticationPrincipal UserDetailsData userDetails,
                                  @PathVariable("id") UUID targetUserId,
                                  RedirectAttributes redirectAttributes) {
        try {
            userService.blockUser(userDetails.getId(), targetUserId);
            redirectAttributes.addFlashAttribute("successMessage", "User blocked successfully.");
        } catch (UserManagementException | AccountClosureException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }

        return new ModelAndView("redirect:/owner/users");
    }

    @PostMapping("/users/{id}/unblock")
    public ModelAndView unblockUser(@AuthenticationPrincipal UserDetailsData userDetails,
                                    @PathVariable("id") UUID targetUserId,
                                    RedirectAttributes redirectAttributes) {
        try {
            userService.unblockUser(userDetails.getId(), targetUserId);
            redirectAttributes.addFlashAttribute("successMessage", "User account restored successfully.");
        } catch (UserManagementException | AccountClosureException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }

        return new ModelAndView("redirect:/owner/users");
    }

    @PostMapping("/users/{id}/make-admin")
    public ModelAndView makeAdmin(@AuthenticationPrincipal UserDetailsData userDetails,
                                  @PathVariable("id") UUID targetUserId,
                                  RedirectAttributes redirectAttributes) {
        try {
            userService.makeAdmin(userDetails.getId(), targetUserId);
            redirectAttributes.addFlashAttribute("successMessage", "User promoted to admin successfully.");
        } catch (UserManagementException | AccountClosureException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }
        return new ModelAndView("redirect:/owner/users");
    }

    @PostMapping("/users/{id}/make-user")
    public ModelAndView makeUser(@AuthenticationPrincipal UserDetailsData userDetails,
                                 @PathVariable("id") UUID targetUserId,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.demoteAdmin(userDetails.getId(), targetUserId);
            redirectAttributes.addFlashAttribute("successMessage", "Admin changed to user successfully.");
        } catch (UserManagementException | AccountClosureException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }
        return new ModelAndView("redirect:/owner/users");
    }

    @GetMapping("/account-closures")
    public ModelAndView getAccountClosures() {
        List<AccountClosureRequestDto> accountClosureRequests =
                accountClosureRequestService.getPendingRequestsForOwner();

        ModelAndView modelAndView = new ModelAndView("owner/account-closures");
        modelAndView.addObject("accountClosureRequests", accountClosureRequests);

        return modelAndView;
    }

    @PostMapping("/account-closures/{id}/reject")
    public ModelAndView rejectAccountClosure(@PathVariable("id") UUID requestId,
                                             RedirectAttributes redirectAttributes) {
        try {
            accountClosureRequestService.rejectRequest(requestId);
            redirectAttributes.addFlashAttribute("successMessage", "Account closure request rejected successfully.");
        } catch (AccountClosureException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }
        return new ModelAndView("redirect:/owner/account-closures");
    }

    @PostMapping("/account-closures/{id}/approve")
    public ModelAndView approveAccountClosure(@PathVariable("id") UUID requestId,
                                              RedirectAttributes redirectAttributes) {
        try {
            accountClosureRequestService.approveRequest(requestId);
            redirectAttributes.addFlashAttribute("successMessage", "Account closure request approved successfully.");
        } catch (AccountClosureException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }
        return new ModelAndView("redirect:/owner/account-closures");
    }

}
