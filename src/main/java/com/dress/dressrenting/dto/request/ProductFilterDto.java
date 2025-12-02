package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Color;
import com.dress.dressrenting.model.enums.Gender;
import com.dress.dressrenting.model.enums.OfferType;
import com.dress.dressrenting.model.enums.ProductCondition;

import java.math.BigDecimal;
import java.util.List;

public record ProductFilterDto(Long categoryId, Long subCategoryId, Long brandId, List<String> sizes, Gender gender,
                               Color color, BigDecimal minPrice,
                               BigDecimal maxPrice, OfferType offerType,
                               ProductCondition productCondition) {
}
