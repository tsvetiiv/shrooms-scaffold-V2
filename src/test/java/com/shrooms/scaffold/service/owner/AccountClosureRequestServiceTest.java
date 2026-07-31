package com.shrooms.scaffold.service.owner;

import com.shrooms.scaffold.event.accountClosure.AccountClosureStatusChangedEvent;
import com.shrooms.scaffold.exception.accountClosure.AccountClosureRequestNotFoundException;
import com.shrooms.scaffold.model.dto.owner.AccountClosureRequestDto;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureRequest;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.accountClosure.AccountClosureRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountClosureRequestServiceTest {

    @Mock
    private AccountClosureRequestRepository accountClosureRequestRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AccountClosureRequestService accountClosureRequestService;

    @Test
    public void getPendingRequestsForOwner_shouldReturnPendingRequestDtos(){
        UUID request1Id = UUID.randomUUID();
        UUID request2Id = UUID.randomUUID();

        User user = User.builder()
                .username("IvanIvan")
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan@gmail.com")
                .build();

        AccountClosureRequest request1 = AccountClosureRequest.builder()
                .id(request1Id)
                .requestedOn(LocalDateTime.now())
                .status(AccountClosureStatus.PENDING)
                .user(user)
                .build();

        AccountClosureRequest request2 = AccountClosureRequest.builder()
                .id(request2Id)
                .requestedOn(LocalDateTime.now())
                .status(AccountClosureStatus.PENDING)
                .user(user)
                .build();

        when(accountClosureRequestRepository
                .findAllByStatusOrderByRequestedOnDesc(AccountClosureStatus.PENDING))
                .thenReturn(List.of(request1, request2));

        List<AccountClosureRequestDto> result =
                accountClosureRequestService.getPendingRequestsForOwner();

        assertEquals(2, result.size());
        assertEquals("IvanIvan", result.get(0).getUsername());
        assertEquals(AccountClosureStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    public void rejectRequest_shouldRejectRequestAndActivateUser(){
        UUID requestId = UUID.randomUUID();

        User user = User.builder()
                .username("IvanIvan")
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan@gmail.com")
                .build();

        AccountClosureRequest request = AccountClosureRequest.builder()
                .id(requestId)
                .requestedOn(LocalDateTime.now())
                .status(AccountClosureStatus.PENDING)
                .user(user)
                .build();

        when(accountClosureRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        accountClosureRequestService.rejectRequest(requestId);

        assertEquals(AccountClosureStatus.REJECTED, request.getStatus());
        assertTrue(user.isActive());
        assertFalse(user.isBlocked());

        verify(accountClosureRequestRepository).save(request);
        verify(publisher).publishEvent(any(AccountClosureStatusChangedEvent.class));
    }

    @Test
    public void rejectRequest_shouldThrowExceptionWhenThereIsNoRequest(){
        UUID requestId = UUID.randomUUID();

        when(accountClosureRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(AccountClosureRequestNotFoundException.class,
                () -> accountClosureRequestService.rejectRequest(requestId));

        verify(accountClosureRequestRepository, never())
                .save(any(AccountClosureRequest.class));

        verify(publisher, never())
                .publishEvent(any(AccountClosureStatusChangedEvent.class));
    }

    @Test
    public void approveRequest_shouldApproveRequestAndBlockUser(){
        UUID requestId = UUID.randomUUID();
        User user = User.builder()
                .username("IvanIvan")
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan@gmail.com")
                .build();

        AccountClosureRequest request = AccountClosureRequest.builder()
                .id(requestId)
                .requestedOn(LocalDateTime.now())
                .status(AccountClosureStatus.PENDING)
                .user(user)
                .build();

        when(accountClosureRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        accountClosureRequestService.approveRequest(requestId);

        assertEquals(AccountClosureStatus.APPROVED, request.getStatus());
        assertFalse(user.isActive());
        assertTrue(user.isBlocked());

        verify(accountClosureRequestRepository).save(request);
        verify(publisher).publishEvent(any(AccountClosureStatusChangedEvent.class));
    }

    @Test
    public void approveRequest_shouldThrowExceptionWhenThereIsNoRequest(){
        UUID requestId = UUID.randomUUID();
        when(accountClosureRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(AccountClosureRequestNotFoundException.class,
                () -> accountClosureRequestService.approveRequest(requestId));

        verify(accountClosureRequestRepository, never())
                .save(any(AccountClosureRequest.class));

        verify(publisher, never())
                .publishEvent(any(AccountClosureStatusChangedEvent.class));
    }

}

