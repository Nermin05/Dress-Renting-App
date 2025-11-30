package com.dress.dressrenting.mapper;

import com.dress.dressrenting.dto.request.ColorAndSizeRequestDto;
import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.response.CategoryResponseDto;
import com.dress.dressrenting.dto.response.ColorAndSizeResponseDto;
import com.dress.dressrenting.dto.response.ProductOfferResponseDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "subCategory", expression = "java(mapSubcategory(productRequestDto.getSubCategoryId()))")
    @Mapping(target = "brand", expression = "java(mapBrand(productRequestDto.getBrandId()))")
    Product toEntity(ProductRequestDto productRequestDto);

    @Mapping(target = "productCode", source = "productCode")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userSurname", source = "user.surname")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userPhone", source = "user.phone")
    @Mapping(target = "subCategory", source = "subCategory")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "genders", expression = "java(product.getSubCategory().getGenders())")
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

    default SubCategory mapSubcategory(Long subcategoryId) {
        if (subcategoryId == null) return null;
        SubCategory subCategory = new SubCategory();
        subCategory.setId(subcategoryId);
        return subCategory;
    }

    default Brand mapBrand(Long brandId) {
        if (brandId == null) return null;
        Brand brand = new Brand();
        brand.setId(brandId);
        return brand;
    }

    default List<ProductOfferResponseDto> mapOffers(List<ProductOffer> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(o -> new ProductOfferResponseDto(
                        o.getId(),
                        o.getOfferType().name(),
                        o.getPrice(),
                        o.getProductCondition().name(),
                        o.getRentDuration()
                ))
                .toList();
    }

    default CategoryResponseDto mapCategoryToDto(Category category) {
        if (category == null) return null;
        return new CategoryResponseDto(category.getId(), category.getName());
    }


}

//    default User mapUser(Long userId) {
//        if (userId == null) return null;
//        User user = new User();
//        user.setId(userId);
//        return user;
//    }
//}