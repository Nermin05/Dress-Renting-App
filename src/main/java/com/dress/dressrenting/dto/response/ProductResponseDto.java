package com.dress.dressrenting.dto.response;

import com.dress.dressrenting.model.Brand;
import com.dress.dressrenting.model.SubCategory;
import com.dress.dressrenting.model.enums.Gender;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductResponseDto(
        String userName,
        String userSurname,
        String userEmail,
        String userPhone,
        String productCode,
        SubCategory subCategory,
        Brand brand,
        BigDecimal price,
        List<Gender> genders,
        String description,
        Long userId,
        List<ColorAndSizeResponseDto> colorAndSizes,
        Instant createdAt,
        List<ProductOfferResponseDto> offers
) {
}
