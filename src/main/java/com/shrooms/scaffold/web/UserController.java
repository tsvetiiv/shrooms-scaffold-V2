package com.shrooms.scaffold.web;

import com.shrooms.scaffold.exception.user.RegistrationException;
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

    private final UserService userService;

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

        try {
            userService.register(userRegisterRequest);
        } catch (RegistrationException exception) {
            bindingResult.addError(new FieldError(
                    "userRegisterRequest",
                    exception.getField(),
                    exception.getMessage()
            ));

            return new ModelAndView("register");
        }

        return new ModelAndView("redirect:/register/success");
    }

    @GetMapping("/register/success")
    public ModelAndView getRegisterSuccessPage() {
        return new ModelAndView("register-success");
    }
}
