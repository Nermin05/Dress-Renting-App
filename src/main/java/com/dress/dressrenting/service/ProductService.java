package com.dress.dressrenting.service;

import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.request.UpdatedProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.enums.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<ProductResponseDto> getAll();

    List<ProductResponseDto> getApprovedProducts();

    List<ProductResponseDto> getAllByOfferType(OfferType offerType, ProductCondition productCondition);


    ProductResponseDto getById(String productCode);

    ProductResponseDto save(ProductRequestDto productRequestDto, List<MultipartFile> images);

    ProductResponseDto update(String productCode, UpdatedProductRequestDto productRequestDto, List<MultipartFile> images);

    void delete(String productCode);

    List<ProductResponseDto> filter(Long subcategoryId,
                                    Color color,
                                    String size,
                                    Gender gender,
                                    BigDecimal minPrice,
                                    BigDecimal maxPrice);

    void changeStatus(String productCode, ProductStatus productStatus);

    List<ProductResponseDto> approveProduct(String productCode);

    List<ProductResponseDto> disapproveProduct(String productCode);
}
