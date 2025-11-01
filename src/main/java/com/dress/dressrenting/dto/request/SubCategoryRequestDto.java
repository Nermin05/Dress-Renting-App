package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubCategoryRequestDto(@NotBlank String name, @NotNull Long categoryId, Gender gender) {
}
