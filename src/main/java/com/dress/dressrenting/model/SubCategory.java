package com.dress.dressrenting.model;

import com.dress.dressrenting.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sub_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Category category;

    @ElementCollection(targetClass = Gender.class, fetch = FetchType.LAZY)
    @CollectionTable(
            name = "subcategory_genders",
            joinColumns = @JoinColumn(name = "subcategory_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    List<Gender> genders = new ArrayList<>();

    @OneToMany(mappedBy = "subCategory")
    @JsonIgnore
    List<Product> products = new ArrayList<>();
}
