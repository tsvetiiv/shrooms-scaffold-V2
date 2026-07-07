package com.shrooms.scaffold.model.dto.owner;

import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountClosureRequestDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime requestedOn;
    private AccountClosureStatus status;
}
