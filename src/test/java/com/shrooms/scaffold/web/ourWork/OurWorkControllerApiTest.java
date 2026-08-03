package com.shrooms.scaffold.web.ourWork;

import com.shrooms.scaffold.model.entity.ourWork.OurWorkProject;
import com.shrooms.scaffold.service.ourWork.OurWorkService;
import com.shrooms.scaffold.web.OurWorkController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OurWorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OurWorkControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OurWorkService ourWorkService;

    @Test
    public void getOurWorkPage_shouldReturnOurWorkPageWithVisibleProjects() throws Exception {
        OurWorkProject project = OurWorkProject.builder()
                .title("Facade scaffold")
                .visible(true)
                .build();

        when(ourWorkService.findVisibleProjects())
                .thenReturn(List.of(project));

        mockMvc.perform(get("/our-work"))
                .andExpect(status().isOk())
                .andExpect(view().name("our-work"))
                .andExpect(model().attributeExists("projects"));

        verify(ourWorkService).findVisibleProjects();
    }
}