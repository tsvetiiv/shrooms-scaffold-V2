package com.shrooms.scaffold.repository.accountClosure;

import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureRequest;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountClosureRequestRepository extends JpaRepository<AccountClosureRequest, UUID> {

    boolean existsByUserIdAndStatus(UUID userId, AccountClosureStatus status);

    List<AccountClosureRequest> findAllByStatusOrderByRequestedOnDesc(AccountClosureStatus status);
}
