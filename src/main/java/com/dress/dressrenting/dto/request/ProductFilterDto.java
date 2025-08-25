package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Color;
import com.dress.dressrenting.model.enums.Gender;

import java.math.BigDecimal;

public record ProductFilterDto(Long subcategoryId, String size, Gender gender, Color color, BigDecimal minPrice,
                               BigDecimal maxPrice) {
}
