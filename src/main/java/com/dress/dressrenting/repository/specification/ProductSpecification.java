package com.dress.dressrenting.repository.specification;

import com.dress.dressrenting.dto.request.ProductFilterDto;
import com.dress.dressrenting.model.Product;
import com.dress.dressrenting.model.enums.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> filter(ProductFilterDto f) {
        return (root, query, cb) -> {
            query.distinct(true);
            var p = cb.conjunction();

            p = cb.and(p, cb.equal(root.get("productStatus"), ProductStatus.ACTIVE));

            if (f.categoryId() != null)
                p = cb.and(p, cb.equal(root.get("subCategory").get("category").get("id"), f.categoryId()));

            if (f.subCategoryId() != null)
                p = cb.and(p, cb.equal(root.get("subCategory").get("id"), f.subCategoryId()));

            if (f.brandId() != null)
                p = cb.and(p, cb.equal(root.get("brand").get("id"), f.brandId()));

            if (f.color() != null || (f.sizes() != null && !f.sizes().isEmpty())) {
                var colorAndSizes = root.join("colorAndSizes");
                if (f.color() != null)
                    p = cb.and(p, cb.equal(colorAndSizes.get("color"), f.color()));
                if (f.sizes() != null && !f.sizes().isEmpty()) {
                    var sizesJoin = colorAndSizes.join("sizes");
                    p = cb.and(p, sizesJoin.in(f.sizes()));
                }
            }

            if (f.gender() != null) {
                var subCat = root.join("subCategory");
                var genders = subCat.join("genders");
                p = cb.and(p, cb.equal(genders, f.gender()));
            }

            if (f.minPrice() != null)
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("price"), f.minPrice()));

            if (f.maxPrice() != null)
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("price"), f.maxPrice()));

            if (f.offerType() != null || f.productCondition() != null) {
                var offerJoin = root.join("productOffers");

                if (f.offerType() != null) {
                    p = cb.and(p, cb.equal(offerJoin.get("offerType"), f.offerType()));
                }

                if (f.productCondition() != null) {
                    p = cb.and(p, cb.equal(offerJoin.get("productCondition"), f.productCondition()));
                }
            }
            return p;
        };
    }

}
