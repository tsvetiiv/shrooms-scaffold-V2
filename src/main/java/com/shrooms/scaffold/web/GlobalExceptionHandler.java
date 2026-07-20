package com.shrooms.scaffold.web;

import com.shrooms.scaffold.Exception.ApplicationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public String handleApplicationException(ApplicationException exception, Model model) {
        model.addAttribute("errorCode", exception.getErrorCode());
        model.addAttribute("errorTitle", exception.getErrorTitle());
        model.addAttribute("errorMessage", exception.getMessage());

        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception exception, Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Unexpected error");
        model.addAttribute("errorMessage", "Something went wrong. Please try again later.");

        return "error";
    }
}
