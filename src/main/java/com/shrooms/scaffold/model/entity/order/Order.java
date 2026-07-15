package com.shrooms.scaffold.model.entity.order;

import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "scaffold_id", nullable = false)
    private Scaffold scaffold;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private boolean installationRequired;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer quantity;

    private Integer rentalWeeks;
    @Column(nullable = false)

    private LocalDateTime createdOn;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String contactPhone;

}
