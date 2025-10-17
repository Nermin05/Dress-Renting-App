package com.dress.dressrenting.dto.request;

import com.dress.dressrenting.model.enums.Color;
import lombok.Data;

import java.util.List;

@Data
public class ColorAndSizeRequestDto {
    private Color color;
    private List<String> sizes;
}
