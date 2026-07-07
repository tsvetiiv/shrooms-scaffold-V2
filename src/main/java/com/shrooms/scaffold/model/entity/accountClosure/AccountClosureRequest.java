package com.shrooms.scaffold.model.entity.accountClosure;

import com.shrooms.scaffold.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_closure_requests")
public class AccountClosureRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountClosureStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedOn;

    private LocalDateTime reviewedOn;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;
}
