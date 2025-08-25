package com.dress.dressrenting.mapper;

import com.dress.dressrenting.dto.request.ColorAndSizeRequestDto;
import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.ColorAndSize;
import com.dress.dressrenting.model.Product;
import com.dress.dressrenting.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", expression = "java(mapUser(productRequestDto.getUserId()))")
    @Mapping(target = "createdAt", ignore = true)
    Product toEntity(ProductRequestDto productRequestDto);

    @Mapping(target = "userId", source = "user.id")
    ProductResponseDto toDto(Product product);

    List<ProductResponseDto> toDtoList(List<Product> products);

    @Mapping(target = "photoCount", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "sizeStockMap", ignore = true)
    ColorAndSize toColorAndSize(ColorAndSizeRequestDto dto);

    List<ColorAndSize> toColorAndSizeList(List<ColorAndSizeRequestDto> dtos);

    default User mapUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
}