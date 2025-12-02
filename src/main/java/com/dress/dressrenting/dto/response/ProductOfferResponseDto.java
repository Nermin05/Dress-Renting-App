package com.dress.dressrenting.dto.response;

import java.math.BigDecimal;

public record ProductOfferResponseDto(
        Long id,
        String offerType,
        BigDecimal price,
        String condition,
        Integer rentDuration
) {
}
