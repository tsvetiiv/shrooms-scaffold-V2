package com.shrooms.scaffold.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/our-work")
public class OurWorkController {

    @GetMapping
    public ModelAndView getOurWorkPage() {
        return new ModelAndView("our-work");
    }
}