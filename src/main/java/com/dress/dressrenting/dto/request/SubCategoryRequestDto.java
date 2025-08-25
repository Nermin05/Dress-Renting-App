package com.dress.dressrenting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubCategoryRequestDto(@NotBlank String name, @NotNull Long categoryId) {
}
