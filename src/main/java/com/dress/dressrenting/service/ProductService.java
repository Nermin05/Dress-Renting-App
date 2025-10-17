package com.dress.dressrenting.service;

import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.request.UpdatedProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.enums.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<ProductResponseDto> getAll();

    List<ProductResponseDto> getPendingProducts();

    List<ProductResponseDto> getApprovedProducts();

    List<ProductResponseDto> getAllByOfferType(OfferType offerType, ProductCondition productCondition);


    ProductResponseDto getById(String productCode);

    ProductResponseDto save(ProductRequestDto productRequestDto, Map<String, List<MultipartFile>> colorImages);

    ProductResponseDto update(String productCode, UpdatedProductRequestDto productRequestDto, List<MultipartFile> images);

    void delete(String productCode);

    List<ProductResponseDto> filter(Long subcategoryId,Long categoryId, Color color, List<String> sizes, Gender gender, BigDecimal minPrice, BigDecimal maxPrice);

    void changeStatus(String productCode, ProductStatus productStatus);

    List<ProductResponseDto> approveProduct(String productCode);

    List<ProductResponseDto> disapproveProduct(String productCode);
}
