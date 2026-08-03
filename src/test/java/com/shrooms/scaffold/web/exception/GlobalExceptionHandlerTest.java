package com.shrooms.scaffold.web.exception;

import com.shrooms.scaffold.exception.ApplicationException;
import com.shrooms.scaffold.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler =
            new GlobalExceptionHandler();

    @Test
    public void handleApplicationException_shouldReturnErrorViewAndAddErrorAttributes() {
        Model model = mock(Model.class);

        ApplicationException exception = new ApplicationException(
                "Not found",
                "404",
                "Resource was not found"
        );

        String viewName =
                globalExceptionHandler.handleApplicationException(exception, model);

        assertEquals("error", viewName);

        verify(model).addAttribute("errorCode", "404");
        verify(model).addAttribute("errorTitle", "Not found");
        verify(model).addAttribute("errorMessage", "Resource was not found");
    }

    @Test
    public void handleUnexpectedException_shouldReturnErrorViewAndAddErrorAttributes() {
        Model model = mock(Model.class);

        Exception exception = new RuntimeException("Boom");

        String viewName =
                globalExceptionHandler.handleUnexpectedException(exception, model);

        assertEquals("error", viewName);

        verify(model).addAttribute("errorCode", "500");
        verify(model).addAttribute("errorTitle", "Unexpected error");
        verify(model).addAttribute("errorMessage", "Something went wrong. Please try again later.");
    }
}
