package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.dto.user.UserEditProfileDto;
import com.shrooms.scaffold.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ModelAndView getProfilePage(HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("user");

        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/profile/edit")
    public ModelAndView getEditProfilePage(HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("user");

        UserEditProfileDto userEditProfileDto = UserEditProfileDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();

        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditProfileDto", userEditProfileDto);

        return modelAndView;
    }

    @PutMapping("/profile/edit")
    public ModelAndView editProfile(@Valid @ModelAttribute("userEditProfileDto") UserEditProfileDto userEditProfileDto,
                                    BindingResult bindingResult,
                                    HttpSession session) {

        if (bindingResult.hasErrors()) {
            UserDto currentUser = (UserDto) session.getAttribute("user");

            ModelAndView modelAndView = new ModelAndView("edit-profile");
            modelAndView.addObject("user", currentUser);
            modelAndView.addObject("userEditProfileDto", userEditProfileDto);

            return modelAndView;
        }

        UserDto currentUser = (UserDto) session.getAttribute("user");
        UserDto updatedUser = userService.editProfile(currentUser.getId(), userEditProfileDto);

        session.setAttribute("user", updatedUser);

        return new ModelAndView("redirect:/users/profile");
    }
}
