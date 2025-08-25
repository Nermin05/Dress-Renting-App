package com.dress.dressrenting.service;

import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.request.UpdatedProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.enums.Color;
import com.dress.dressrenting.model.enums.Gender;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<ProductResponseDto> getAll();
    ProductResponseDto getById(String productCode);
    ProductResponseDto save(ProductRequestDto productRequestDto, List<MultipartFile> images);
    ProductResponseDto update(String productCode, UpdatedProductRequestDto productRequestDto,List<MultipartFile> images);
    void delete(String productCode);
    List<ProductResponseDto> filter(Long subcategoryId,
                                    Color color,
                                    String size,
                                    Gender gender,
                                    BigDecimal minPrice,
                                    BigDecimal maxPrice);
}
