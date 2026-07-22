package com.shrooms.scaffold.service;

import com.shrooms.scaffold.model.dto.scaffold.ScaffoldRequest;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.scaffold.ScaffoldCategory;
import com.shrooms.scaffold.repository.order.OrderRepository;
import com.shrooms.scaffold.repository.scaffold.ScaffoldRepository;
import com.shrooms.scaffold.service.scaffold.ScaffoldService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

}
