package com.dress.dressrenting.model;

import com.dress.dressrenting.model.enums.OfferType;
import com.dress.dressrenting.model.enums.ProductCondition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "product_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    Product product;

    @Enumerated(EnumType.STRING)
    OfferType offerType;

    BigDecimal price;

    Integer rentDuration;

    @Enumerated(EnumType.STRING)
    ProductCondition productCondition;
}
