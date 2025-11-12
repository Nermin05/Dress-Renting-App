package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Color;
import com.dress.dressrenting.model.enums.Gender;

import java.math.BigDecimal;
import java.util.List;

public record ProductFilterDto(Long categoryId,List<String> sizes, Gender gender, Color color, BigDecimal minPrice,
                               BigDecimal maxPrice) {
}
