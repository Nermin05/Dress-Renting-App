package com.dress.dressrenting.model;

import com.dress.dressrenting.model.enums.Gender;
import com.dress.dressrenting.model.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, unique = true)
    String productCode;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    Long subcategoryId;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<ColorAndSize> colorAndSizes;
    BigDecimal price;
    @Enumerated(EnumType.STRING)
    Gender gender;
    @Enumerated(EnumType.STRING)
    ProductStatus productStatus;
    Instant createdAt;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductOffer> productOffers;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        productStatus = ProductStatus.ACTIVE;

    }
}
