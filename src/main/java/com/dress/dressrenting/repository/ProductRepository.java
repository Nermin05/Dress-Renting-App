package com.dress.dressrenting.repository;

import com.dress.dressrenting.model.Product;
import com.dress.dressrenting.model.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByProductCode(String productCode);

    void deleteByProductCode(String productCode);

    List<Product> findByProductStatus(ProductStatus productStatus);
}
