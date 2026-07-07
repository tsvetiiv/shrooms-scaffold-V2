package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.dto.user.UserLoginRequest;
import com.shrooms.scaffold.model.entity.user.RoleType;
import com.shrooms.scaffold.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    private final UserService userService;


    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("userLoginData", userLoginRequest);


        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute("userLoginData") UserLoginRequest userLoginRequest,
                              BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("login");
        }
        try {
            UserDto user = userService.login(userLoginRequest);
            session.setAttribute("user", user);

            if (user.getRoleType() == RoleType.ADMIN) {
                return new ModelAndView("redirect:/admin");
            }
            return new ModelAndView("redirect:/");


        } catch (RuntimeException exception) {

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("login");
            modelAndView.addObject("userLoginData", userLoginRequest);
            modelAndView.addObject("loginError",
                    exception.getMessage());

            return modelAndView;
        }
    }
}
