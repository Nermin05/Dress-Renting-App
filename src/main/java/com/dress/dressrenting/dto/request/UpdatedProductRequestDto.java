package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Color;
import com.dress.dressrenting.model.enums.Gender;

import java.math.BigDecimal;
import java.util.List;


public record UpdatedProductRequestDto(Color color,
                                       BigDecimal price, List<Gender> genders,
                                       List<String> keptImageUrls,
                                       List<ProductOfferRequestDto> productOffers) {
}
