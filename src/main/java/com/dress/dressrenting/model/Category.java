package com.dress.dressrenting.model;

import com.dress.dressrenting.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@ToString(onlyExplicitlyIncluded = true)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    @ElementCollection(targetClass = Gender.class, fetch = FetchType.LAZY)
    @CollectionTable(
            name = "category_genders",
            joinColumns = @JoinColumn(name = "category_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    List<Gender> genders = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    List<Product> products = new ArrayList<>();

//    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    List<SubCategory> subCategories = new ArrayList<>();

}
