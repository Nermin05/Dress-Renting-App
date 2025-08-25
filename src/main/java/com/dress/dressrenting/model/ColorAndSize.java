package com.dress.dressrenting.model;

import com.dress.dressrenting.model.enums.Color;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "product_color_sizes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColorAndSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    Color color;

    Integer photoCount;

    Long stock;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "color_size_id"))
    @Column(name = "image_url")
    List<String> imageUrls;

    @ElementCollection
    @CollectionTable(name = "product_size_stock", joinColumns = @JoinColumn(name = "color_size_id"))
    @MapKeyColumn(name = "size")
    @Column(name = "stock")
    Map<String, Long> sizeStockMap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;
}
