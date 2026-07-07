package com.shrooms.scaffold.model.entity.scaffold;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "scaffolds")
public class Scaffold {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false,columnDefinition = "TEXT")
    private String description;

    private double height;
    private double width;
    private double length;

    @Column(name = "material_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MaterialType materialType;

    @Column(nullable = false, name = "price_for_sale")
    private BigDecimal priceForSale;

    @Column(nullable = false, name = "price_for_rent")
    private BigDecimal priceForRent;

    private String imageUrl;

    private boolean available;

    @Column(name = "scaffold_category",  nullable = false)
    @Enumerated(EnumType.STRING)
    private ScaffoldCategory scaffoldCategory;
}
