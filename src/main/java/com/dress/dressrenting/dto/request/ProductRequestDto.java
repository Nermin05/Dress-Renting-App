package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Gender;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequestDto {
    private Long userId;
    private Long subcategoryId;
    private List<ColorAndSizeRequestDto> colorAndSizes;
    private Gender gender;
    private List<ProductOfferRequestDto> productOffers;
}

