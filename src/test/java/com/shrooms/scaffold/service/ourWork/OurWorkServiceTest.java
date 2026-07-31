package com.shrooms.scaffold.service.ourWork;

import com.shrooms.scaffold.exception.ourWork.ProjectNotFoundException;
import com.shrooms.scaffold.model.dto.ourWork.OurWorkProjectRequest;
import com.shrooms.scaffold.model.entity.ourWork.OurWorkProject;
import com.shrooms.scaffold.repository.ourWork.OurWorkProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OurWorkServiceTest {

    @Mock
    private OurWorkProjectRepository ourWorkProjectRepository;

    @InjectMocks
    private OurWorkService ourWorkService;

    @Test
    public void createProject_shouldCreateAndSaveProject() {
        OurWorkProjectRequest request = new OurWorkProjectRequest();
        request.setDescription("Some description for test");
        request.setTitle("Scaffold for test");
        request.setVisible(true);
        request.setImageUrl("image.png");

        ourWorkService.createProject(request);

        verify(ourWorkProjectRepository).save(any(OurWorkProject.class));
    }

    @Test
    public void getProjectForEdit_shouldReturnProjectRequestWhenProjectExists() {
        UUID projectId = UUID.randomUUID();

        OurWorkProject project = OurWorkProject.builder()
                .id(projectId)
                .title("Scaffold for test")
                .description("Some description")
                .imageUrl("image.png")
                .visible(true)
                .build();

        when(ourWorkProjectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        OurWorkProjectRequest result = ourWorkService.getProjectForEdit(projectId);

        assertEquals("Scaffold for test", result.getTitle());
        assertEquals("Some description", result.getDescription());
        assertEquals("image.png", result.getImageUrl());
        assertTrue(result.isVisible());

        verify(ourWorkProjectRepository).findById(projectId);
    }

    @Test
    public void getProjectForEdit_shouldThrowExceptionWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();

        when(ourWorkProjectRepository.findById(projectId))
                .thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class,
                () -> ourWorkService.getProjectForEdit(projectId));
    }

    @Test
    public void updateProject_shouldUpdateAndSaveProject() {
        UUID projectId = UUID.randomUUID();

        OurWorkProject project = OurWorkProject.builder()
                .id(projectId)
                .title("Scaffold for test")
                .description("Some description")
                .imageUrl("image.png")
                .visible(true)
                .build();

        OurWorkProjectRequest request = new OurWorkProjectRequest();
        request.setDescription("Updated description for test");
        request.setTitle("Updated scaffold");
        request.setVisible(true);
        request.setImageUrl("updated-image.png");

        when(ourWorkProjectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        ourWorkService.updateProject(projectId, request);

        assertEquals("Updated scaffold", project.getTitle());
        assertEquals("Updated description for test", project.getDescription());
        assertEquals("updated-image.png", project.getImageUrl());
        assertTrue(project.isVisible());

        verify(ourWorkProjectRepository).save(project);
    }

    @Test
    public void updateProject_shouldThrowExceptionWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();

        when(ourWorkProjectRepository.findById(projectId))
                .thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class,
                () -> ourWorkService.updateProject(projectId, new OurWorkProjectRequest()));

        verify(ourWorkProjectRepository, never()).save(any(OurWorkProject.class));
    }

    @Test
    public void deleteProject_shouldDeleteProject() {
        UUID projectId = UUID.randomUUID();

        OurWorkProject project = OurWorkProject.builder()
                .id(projectId)
                .title("Scaffold for test")
                .description("Some description")
                .imageUrl("image.png")
                .visible(true)
                .build();

        when(ourWorkProjectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        ourWorkService.deleteProject(projectId);
        verify(ourWorkProjectRepository).delete(project);
    }

    @Test
    public void deleteProject_shouldThrowExceptionWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();

        when(ourWorkProjectRepository.findById(projectId))
                .thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class,
                () -> ourWorkService.deleteProject(projectId));

        verify(ourWorkProjectRepository, never()).delete(any(OurWorkProject.class));
    }
}
