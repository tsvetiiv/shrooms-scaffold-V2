package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.user.UserRegisterRequest;
import com.shrooms.scaffold.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("userRegisterRequest", userRegisterRequest);

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(
            @Valid @ModelAttribute("userRegisterRequest") UserRegisterRequest userRegisterRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getConfirmPassword())) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("userRegisterRequest", userRegisterRequest);
            modelAndView.addObject("passwordError", "Passwords do not match!");
            return modelAndView;
        }

        try {
            userService.register(userRegisterRequest);
        } catch (RuntimeException exception) {
            if ("Username already exists".equals(exception.getMessage())) {
                bindingResult.addError(new FieldError(
                        "userRegisterRequest",
                        "username",
                        exception.getMessage()));
            } else if ("Email already exists".equals(exception.getMessage())) {
                bindingResult.addError(new FieldError(
                        "userRegisterRequest",
                        "email",
                        exception.getMessage()));
            } else {
                throw exception;
            }
            return new ModelAndView("register");
        }

        return new ModelAndView("redirect:/register/success");
    }

    @GetMapping("/register/success")
    public ModelAndView getRegisterSuccessPage() {
        return new ModelAndView("register-success");
    }
}
