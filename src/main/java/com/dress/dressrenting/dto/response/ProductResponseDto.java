package com.dress.dressrenting.dto.response;

import com.dress.dressrenting.model.ProductOffer;
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
        SubCategory subcategory,
        BigDecimal price,
        Gender gender,
        String description,
        Long userId,
        List<ColorAndSizeResponseDto> colorAndSizes,
        Instant createdAt,
        List<ProductOffer> offers) {
}
