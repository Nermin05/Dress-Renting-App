package com.dress.dressrenting.mapper;

import com.dress.dressrenting.dto.request.ColorAndSizeRequestDto;
import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.response.ColorAndSizeResponseDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.Category;
import com.dress.dressrenting.model.ColorAndSize;
import com.dress.dressrenting.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category", expression = "java(mapCategory(productRequestDto.getCategoryId()))")
    Product toEntity(ProductRequestDto productRequestDto);

    @Mapping(target = "productCode", source = "productCode")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "userSurname", source = "user.surname")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userPhone", source = "user.phone")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "genders", expression = "java(product.getCategory().getGenders())")
    @Mapping(target = "colorAndSizes", expression = "java(mapColorAndSizes(product.getColorAndSizes()))")
    @Mapping(target = "offers", source = "productOffers")
    ProductResponseDto toDto(Product product);


    default List<ColorAndSizeResponseDto> mapColorAndSizes(List<ColorAndSize> list) {
        if (list == null) return List.of();
        return list.stream().map(cs -> {
            ColorAndSizeResponseDto dto = new ColorAndSizeResponseDto();
            dto.setColor(cs.getColor() != null ? cs.getColor().name() : null);
            dto.setSizes(cs.getSizes());
            dto.setImageUrls(cs.getImageUrls());
            return dto;
        }).collect(Collectors.toList());
    }

    List<ProductResponseDto> toDtoList(List<Product> products);

    @Mapping(target = "photoCount", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    ColorAndSize toColorAndSize(ColorAndSizeRequestDto dto);

    List<ColorAndSize> toColorAndSizeList(List<ColorAndSizeRequestDto> dtos);

    default Category mapCategory(Long categoryId) {
        if (categoryId == null) return null;
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}

//    default User mapUser(Long userId) {
//        if (userId == null) return null;
//        User user = new User();
//        user.setId(userId);
//        return user;
//    }
//}