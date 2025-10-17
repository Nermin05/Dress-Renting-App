package com.dress.dressrenting.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ColorAndSizeResponseDto {
    private String color;
    private List<String> sizes;
    private List<String> imageUrls;
}
