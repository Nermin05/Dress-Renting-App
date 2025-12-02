package com.dress.dressrenting.dto.response;

import com.dress.dressrenting.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductResponseDto(
        Long id,
        String productCode,

        Long userId,
        String userName,
        String userSurname,
        String userEmail,
        String userPhone,

        Long subCategoryId,
        String subCategoryName,

        Long brandId,
        String brandName,

        BigDecimal price,
        List<Gender> genders,
        String description,

        List<ColorAndSizeResponseDto> colorAndSizes,
        List<ProductOfferResponseDto> offers,

        Instant createdAt
) {
}

