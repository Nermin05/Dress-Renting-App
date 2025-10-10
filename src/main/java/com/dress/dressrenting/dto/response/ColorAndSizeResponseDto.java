package com.dress.dressrenting.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ColorAndSizeResponseDto {
    private String color;
    private String size;
    private List<String> imageUrls;
}
