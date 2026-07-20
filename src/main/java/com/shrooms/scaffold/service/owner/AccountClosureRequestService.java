package com.shrooms.scaffold.service.owner;

import com.shrooms.scaffold.Exception.accountClosure.AccountClosureRequestNotFoundException;
import com.shrooms.scaffold.Exception.accountClosure.AccountClosureException;
import com.shrooms.scaffold.event.accountClosure.AccountClosureStatusChangedEvent;
import com.shrooms.scaffold.model.dto.owner.AccountClosureRequestDto;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureRequest;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.accountClosure.AccountClosureRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountClosureRequestService {

    private final AccountClosureRequestRepository accountClosureRequestRepository;
    private final ApplicationEventPublisher publisher;

    public AccountClosureRequestService(AccountClosureRequestRepository accountClosureRequestRepository, ApplicationEventPublisher publisher) {
        this.accountClosureRequestRepository = accountClosureRequestRepository;
        this.publisher = publisher;
    }

    public List<AccountClosureRequestDto> getPendingRequestsForOwner() {
        return accountClosureRequestRepository
                .findAllByStatusOrderByRequestedOnDesc(AccountClosureStatus.PENDING)
                .stream()
                .map(request -> AccountClosureRequestDto.builder()
                        .id(request.getId())
                        .username(request.getUser().getUsername())
                        .firstName(request.getUser().getFirstName())
                        .lastName(request.getUser().getLastName())
                        .email(request.getUser().getEmail())
                        .requestedOn(request.getRequestedOn())
                        .status(request.getStatus())
                        .build())
                .toList();
    }

    @Transactional
    public void rejectRequest(UUID requestId) {
        AccountClosureRequest request = accountClosureRequestRepository.findById(requestId)
                .orElseThrow(AccountClosureRequestNotFoundException::new);

        if (request.getStatus() != AccountClosureStatus.PENDING) {
            throw new AccountClosureException("This request is already reviewed");
        }

        User user = request.getUser();
        user.setBlocked(false);
        user.setActive(true);

        request.setStatus(AccountClosureStatus.REJECTED);
        request.setReviewedOn(LocalDateTime.now());

        accountClosureRequestRepository.save(request);

        AccountClosureStatusChangedEvent event = new AccountClosureStatusChangedEvent(
                user.getUsername(),
                user.getEmail(),
                request.getStatus(),
                user.isActive()
        );
        publisher.publishEvent(event);
    }

    @Transactional
    public void approveRequest(UUID requestId) {

        AccountClosureRequest request = accountClosureRequestRepository.findById(requestId)
                .orElseThrow(AccountClosureRequestNotFoundException::new);

        if (request.getStatus() != AccountClosureStatus.PENDING) {
            throw new AccountClosureException("This request is already reviewed");
        }

        User user = request.getUser();
        user.setBlocked(true);
        user.setActive(false);

        request.setStatus(AccountClosureStatus.APPROVED);
        request.setReviewedOn(LocalDateTime.now());

        accountClosureRequestRepository.save(request);

        AccountClosureStatusChangedEvent event = new AccountClosureStatusChangedEvent(
                user.getUsername(),
                user.getEmail(),
                request.getStatus(),
                user.isActive()
        );
        publisher.publishEvent(event);
    }
}

