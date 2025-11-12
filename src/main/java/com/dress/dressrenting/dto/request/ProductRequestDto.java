package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequestDto {
    @NotBlank
    private String userName;
    @NotBlank
    private String userSurname;
    @NotBlank
    @Email
    private String userEmail;
    @NotBlank
    private String description;
    @NotBlank
    @Pattern(
            regexp = "^(\\+994|0)(50|51|55|70|77|99)\\d{7}$"
    )
    private String userPhone;
    private Long categoryId;
    private List<ColorAndSizeRequestDto> colorAndSizes;
    private List<Gender> genders;
    private List<ProductOfferRequestDto> productOffers;
}

