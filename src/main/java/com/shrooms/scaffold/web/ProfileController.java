package com.shrooms.scaffold.web;

import com.shrooms.scaffold.Exception.accountClosure.AccountClosureException;
import com.shrooms.scaffold.Exception.user.RegistrationException;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.dto.user.UserEditProfileDto;
import com.shrooms.scaffold.service.user.UserDetailsData;
import com.shrooms.scaffold.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ModelAndView getProfilePage(@AuthenticationPrincipal UserDetailsData userDetails) {

        UserDto user = userService.getUserById(userDetails.getId());

        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/profile/edit")
    public ModelAndView getEditProfilePage(@AuthenticationPrincipal UserDetailsData userDetails) {

        UserDto user = userService.getUserById(userDetails.getId());

        UserEditProfileDto userEditProfileDto = UserEditProfileDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();

        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditProfileDto", userEditProfileDto);
        addAccountClosureAttributes(modelAndView, userDetails.getId());

        return modelAndView;
    }

    @PutMapping("/profile/edit")
    public ModelAndView editProfile(@Valid @ModelAttribute("userEditProfileDto") UserEditProfileDto userEditProfileDto,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal UserDetailsData userDetails) {

        if (bindingResult.hasErrors()) {
            UserDto currentUser = userService.getUserById(userDetails.getId());

            ModelAndView modelAndView = new ModelAndView("edit-profile");
            modelAndView.addObject("user", currentUser);
            modelAndView.addObject("userEditProfileDto", userEditProfileDto);
            addAccountClosureAttributes(modelAndView, userDetails.getId());

            return modelAndView;
        }

        try {
            userService.editProfile(userDetails.getId(), userEditProfileDto);
        } catch (RegistrationException exception) {
            UserDto currentUser = userService.getUserById(userDetails.getId());

            bindingResult.addError(new FieldError(
                    "userEditProfileDto",
                    exception.getField(),
                    exception.getMessage()
            ));

            ModelAndView modelAndView = new ModelAndView("edit-profile");
            modelAndView.addObject("user", currentUser);
            modelAndView.addObject("userEditProfileDto", userEditProfileDto);
            addAccountClosureAttributes(modelAndView, userDetails.getId());

            return modelAndView;
        }

        return new ModelAndView("redirect:/users/profile");
    }

    @PostMapping("/profile/close-account")
    public ModelAndView requestAccountClosure(@AuthenticationPrincipal UserDetailsData userDetails,
                                              RedirectAttributes redirectAttributes) {
        try {
            userService.requestAccountClosure(userDetails.getId());
            userDetails.setActive(false);

            redirectAttributes.addFlashAttribute("accountClosureMessage",
                    "Your account closure request is waiting for owner approval.");
        } catch (AccountClosureException exception) {
            redirectAttributes.addFlashAttribute("accountClosureError", exception.getMessage());
        }

        return new ModelAndView("redirect:/users/profile/edit");
    }

    private void addAccountClosureAttributes(ModelAndView modelAndView, UUID userId) {
        boolean accountClosurePending = userService.hasPendingAccountClosureRequest(userId);
        boolean hasUnfinishedOrders = userService.hasUnfinishedOrders(userId);

        modelAndView.addObject("accountClosurePending", accountClosurePending);
        modelAndView.addObject("hasUnfinishedOrders", hasUnfinishedOrders);
    }
}
