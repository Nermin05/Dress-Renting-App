package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CategoryRequestDto(@NotBlank String name, List<Gender> genders) {
}
