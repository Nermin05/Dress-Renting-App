package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.OfferType;
import com.dress.dressrenting.model.enums.ProductCondition;

import java.math.BigDecimal;

public record ProductOfferRequestDto(OfferType offerType, BigDecimal price,
                                     Integer rentDuration, ProductCondition condition) {
}
