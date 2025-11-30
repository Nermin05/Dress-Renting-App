package com.dress.dressrenting.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BrandRequestDto(@NotBlank String name) {
}
