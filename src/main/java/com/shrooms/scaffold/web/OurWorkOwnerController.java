package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.ourWork.OurWorkProjectRequest;
import com.shrooms.scaffold.model.entity.ourWork.OurWorkProject;
import com.shrooms.scaffold.service.ourWork.OurWorkService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/owner/our-work")
public class OurWorkOwnerController {

    private final OurWorkService ourWorkService;

    public OurWorkOwnerController(OurWorkService ourWorkService) {
        this.ourWorkService = ourWorkService;
    }

    @GetMapping
    public ModelAndView getAllProjects() {

        List<OurWorkProject> projects = ourWorkService.findAll();
        ModelAndView modelAndView = new ModelAndView("owner/our-work");
        modelAndView.addObject("projects", projects);
        return modelAndView;

    }

    @GetMapping("/create")
    public ModelAndView getCreateProjectPage() {
        ModelAndView modelAndView = new ModelAndView("owner/create-our-work");
        modelAndView.addObject("ourWorkProjectRequest", new OurWorkProjectRequest());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createProject(
            @Valid @ModelAttribute("ourWorkProjectRequest") OurWorkProjectRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("owner/create-our-work");
            modelAndView.addObject("ourWorkProjectRequest", request);
            return modelAndView;
        }

        ourWorkService.createProject(request);
        return new ModelAndView("redirect:/owner/our-work");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView getEditProjectPage(@PathVariable UUID id) {
        OurWorkProjectRequest request = ourWorkService.getProjectForEdit(id);

        ModelAndView modelAndView = new ModelAndView("owner/edit-our-work");
        modelAndView.addObject("projectId", id);
        modelAndView.addObject("ourWorkProjectRequest", request);

        return modelAndView;
    }

    @PutMapping("/{id}")
    public ModelAndView updateProject(
            @PathVariable UUID id,
            @Valid @ModelAttribute("ourWorkProjectRequest") OurWorkProjectRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("owner/edit-our-work");
            modelAndView.addObject("projectId", id);
            modelAndView.addObject("ourWorkProjectRequest", request);
            return modelAndView;
        }

        ourWorkService.updateProject(id, request);
        return new ModelAndView("redirect:/owner/our-work");
    }

    @DeleteMapping("/{id}/hide")
    public ModelAndView hideProject(@PathVariable UUID id) {
        ourWorkService.hideProject(id);
        return new ModelAndView("redirect:/owner/our-work");
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteProject(@PathVariable UUID id) {
        ourWorkService.deleteProject(id);
        return new ModelAndView("redirect:/owner/our-work");
    }
}
