package com.shrooms.scaffold.web;

import com.shrooms.scaffold.service.ourWork.OurWorkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/our-work")
public class OurWorkController {

    private final OurWorkService ourWorkService;

    public OurWorkController(OurWorkService ourWorkService) {
        this.ourWorkService = ourWorkService;
    }

    @GetMapping
    public ModelAndView getOurWorkPage() {
        ModelAndView modelAndView = new ModelAndView("our-work");
        modelAndView.addObject("projects", ourWorkService.findVisibleProjects());
        return modelAndView;
    }
}