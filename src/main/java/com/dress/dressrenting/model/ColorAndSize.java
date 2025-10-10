package com.dress.dressrenting.model;

import com.dress.dressrenting.model.enums.Color;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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

    String size;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "color_size_id"))
    @Column(name = "image_url")
    List<String> imageUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    Product product;
}
