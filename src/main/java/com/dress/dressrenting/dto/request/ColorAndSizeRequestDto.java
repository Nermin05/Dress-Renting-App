package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Color;
import lombok.Data;

@Data
public class ColorAndSizeRequestDto {
    private Color color;
    private String size;
}
