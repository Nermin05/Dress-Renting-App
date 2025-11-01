package com.dress.dressrenting.mapper;

import com.dress.dressrenting.dto.request.ColorAndSizeRequestDto;
import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.response.ColorAndSizeResponseDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.ColorAndSize;
import com.dress.dressrenting.model.Product;
import com.dress.dressrenting.model.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "subcategory", expression = "java(mapSubCategory(productRequestDto.getSubcategoryId()))")
    Product toEntity(ProductRequestDto productRequestDto);

    @Mapping(target = "productCode", source = "productCode")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "userSurname", source = "user.surname")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userPhone", source = "user.phone")
    @Mapping(target = "subcategory", source = "subcategory")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "gender", expression = "java(product.getSubcategory().getGender())")
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

    default SubCategory mapSubCategory(Long subCategoryId) {
        if (subCategoryId == null) return null;
        SubCategory subCategory = new SubCategory();
        subCategory.setId(subCategoryId);
        return subCategory;
    }
}

//    default User mapUser(Long userId) {
//        if (userId == null) return null;
//        User user = new User();
//        user.setId(userId);
//        return user;
//    }
//}