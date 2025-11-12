package com.dress.dressrenting.repository.specification;

import com.dress.dressrenting.dto.request.ProductFilterDto;
import com.dress.dressrenting.model.ColorAndSize;
import com.dress.dressrenting.model.Product;
import com.dress.dressrenting.model.enums.ProductStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> filter(ProductFilterDto productFilterDto) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicates = criteriaBuilder.conjunction();

            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("productStatus"), ProductStatus.ACTIVE));


            if (productFilterDto.categoryId() != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("subcategory").get("category").get("id"), productFilterDto.categoryId()));
            }

            if (productFilterDto.color() != null || productFilterDto.sizes() != null) {
                Join<Product, ColorAndSize> colorAndSizes = root.join("colorAndSizes");
                if (productFilterDto.color() != null) {
                    predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(colorAndSizes.get("color"), productFilterDto.color()));
                }
                if (productFilterDto.sizes() != null && !productFilterDto.sizes().isEmpty()) {
                    Join<ColorAndSize, String> sizesJoin = colorAndSizes.join("sizes");
                    predicates = criteriaBuilder.and(predicates, sizesJoin.in(productFilterDto.sizes()));
                }
            }
            if (productFilterDto.gender() != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("subcategory").get("gender"),
                                productFilterDto.gender()
                        )
                );
            }
            if (productFilterDto.minPrice() != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("price"),
                        productFilterDto.minPrice()));
            }
            if (productFilterDto.maxPrice() != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.lessThanOrEqualTo(root.get("price"), productFilterDto.maxPrice()));
            }

            return predicates;
        };
    }
}
