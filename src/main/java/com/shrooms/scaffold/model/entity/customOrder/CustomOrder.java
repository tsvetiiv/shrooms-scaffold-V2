package com.shrooms.scaffold.model.entity.customOrder;


import com.shrooms.scaffold.model.entity.order.OrderType;
import com.shrooms.scaffold.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "custom_order")
public class CustomOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double height;
    @Column(nullable = false)
    private Double width;
    @Column(nullable = false)
    private Double length;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private boolean installationRequired;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String projectName;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    private String projectImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    private BigDecimal estimatedPrice;

    @Column(nullable = false)
    private LocalDate createdOn;

}
