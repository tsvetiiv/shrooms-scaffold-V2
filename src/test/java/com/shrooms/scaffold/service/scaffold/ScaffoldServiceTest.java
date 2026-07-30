package com.shrooms.scaffold.service.scaffold;

import com.shrooms.scaffold.exception.scaffold.ScaffoldNotFoundException;
import com.shrooms.scaffold.model.dto.scaffold.ScaffoldRequest;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.scaffold.ScaffoldCategory;
import com.shrooms.scaffold.repository.order.OrderRepository;
import com.shrooms.scaffold.repository.scaffold.ScaffoldRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScaffoldServiceTest {

    @Mock
    private ScaffoldRepository scaffoldRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ScaffoldService scaffoldService;


    @Test
    public void createScaffold_shouldSaveScaffold() {
        ScaffoldRequest request = ScaffoldRequest.builder()
                .name("Scaffold")
                .description("description")
                .height(13.0)
                .width(12.0)
                .scaffoldCategory(ScaffoldCategory.FACADE)
                .build();
        scaffoldService.createScaffold(request);

        verify(scaffoldRepository).save(any(Scaffold.class));
    }

    @Test
    public void deleteScaffold_shouldDeleteScaffoldWhenNoOrdersExist() {
        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .name("Scaffold")
                .description("description")
                .height(13.0)
                .width(12.0)
                .scaffoldCategory(ScaffoldCategory.FACADE)
                .build();

        when(scaffoldRepository.findById(scaffoldId))
                .thenReturn(Optional.of(scaffold));
        when(orderRepository.existsByScaffoldId(scaffoldId))
                .thenReturn(false);
        boolean result = scaffoldService.deleteScaffold(scaffoldId);

        assertTrue(result);

        verify(scaffoldRepository).deleteById(scaffoldId);
    }

    @Test
    public void deleteScaffold_shouldMakeScaffoldUnavailableWhenOrdersExist(){
        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .name("Scaffold")
                .description("description")
                .height(13.0)
                .width(12.0)
                .scaffoldCategory(ScaffoldCategory.FACADE)
                .build();

        when(scaffoldRepository.findById(scaffoldId))
                .thenReturn(Optional.of(scaffold));
        when(orderRepository.existsByScaffoldId(scaffoldId))
                .thenReturn(true);

        boolean result = scaffoldService.deleteScaffold(scaffoldId);

        assertFalse(result);
        assertFalse(scaffold.isAvailable());

        verify(scaffoldRepository).save(scaffold);
    }

    @Test
    public void getScaffoldForEdit_shouldReturnScaffoldRequestWhenScaffoldExists(){
        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = new Scaffold();
        scaffold.setId(scaffoldId);
        scaffold.setName("Scaffold");
        scaffold.setDescription("description");
        scaffold.setHeight(13.0);
        scaffold.setWidth(12.0);
        scaffold.setScaffoldCategory(ScaffoldCategory.FACADE);
        scaffold.setAvailable(true);

        when(scaffoldRepository.findById(scaffoldId))
                .thenReturn(Optional.of(scaffold));

        ScaffoldRequest result = scaffoldService.getScaffoldForEdit(scaffoldId);

        assertEquals("Scaffold", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(13.0, result.getHeight());
        assertEquals(12.0, result.getWidth());
        assertEquals(ScaffoldCategory.FACADE, result.getScaffoldCategory());
        assertTrue(result.isAvailable());

        verify(scaffoldRepository).findById(scaffoldId);
    }

    @Test
    public void getScaffoldForEdit_shouldThrowExceptionWhenScaffoldDoesNotExist(){
        UUID scaffoldId = UUID.randomUUID();

        when(scaffoldRepository.findById(scaffoldId))
                .thenReturn(Optional.empty());

        assertThrows(ScaffoldNotFoundException.class,
                () ->  scaffoldService.getScaffoldForEdit(scaffoldId));
        verify(scaffoldRepository).findById(scaffoldId);
    }

    @Test
    public void findById_shouldReturnScaffoldWhenScaffoldExists(){
        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = new Scaffold();
        scaffold.setId(scaffoldId);
        scaffold.setName("Scaffold");
        scaffold.setDescription("description");
        scaffold.setHeight(13.0);
        scaffold.setWidth(12.0);
        scaffold.setScaffoldCategory(ScaffoldCategory.FACADE);
        scaffold.setAvailable(true);

        when(scaffoldRepository.findById(scaffoldId))
                .thenReturn(Optional.of(scaffold));

        Scaffold result = scaffoldService.findById(scaffoldId);

        assertEquals(scaffold, result);

        verify(scaffoldRepository).findById(scaffoldId);
    }

    @Test
    public void findById_shouldThrowExceptionWhenScaffoldDoesNotExist(){
        UUID scaffoldId = UUID.randomUUID();

        when(scaffoldRepository.findById(scaffoldId))
                .thenReturn(Optional.empty());

        assertThrows(ScaffoldNotFoundException.class,
                () -> scaffoldService.findById(scaffoldId));
        verify(scaffoldRepository).findById(scaffoldId);
    }
}
