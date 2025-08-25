package com.dress.dressrenting.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String name, @NotBlank String surname, @NotBlank String email,
                              @NotBlank String password, @NotBlank String phone) {
}
