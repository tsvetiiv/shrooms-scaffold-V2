package com.shrooms.scaffold.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public ModelAndView getHomePage(Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            boolean isOwner = authentication.getAuthorities()
                    .stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_OWNER"));

            if (isOwner) {
                return new ModelAndView("redirect:/owner");
            }

            boolean isAdmin = authentication.getAuthorities()
                    .stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return new ModelAndView("redirect:/admin");
            }
        }

        return new ModelAndView("index");
    }
}