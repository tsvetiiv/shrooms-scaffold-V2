package com.shrooms.scaffold.web;

import com.shrooms.scaffold.service.scaffold.ScaffoldService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private final ScaffoldService scaffoldService;

    public HomeController(ScaffoldService scaffoldService) {
        this.scaffoldService = scaffoldService;
    }

    @GetMapping("/")
    public ModelAndView getHomePage() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("index");
        modelAndView.addObject("scaffolds", scaffoldService.findAll());

        return modelAndView;
    }
}