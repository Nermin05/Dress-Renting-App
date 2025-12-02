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

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "subCategory", expression = "java(mapSubcategory(productRequestDto.getSubCategoryId()))")
    @Mapping(target = "brand", expression = "java(mapBrand(productRequestDto.getBrandId()))")
    Product toEntity(ProductRequestDto productRequestDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "productCode", source = "productCode")

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "userSurname", source = "user.surname")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userPhone", source = "user.phone")

    @Mapping(target = "subCategoryId",
            expression = "java(product.getSubCategory()==null? null : product.getSubCategory().getId())")
    @Mapping(target = "subCategoryName",
            expression = "java(product.getSubCategory()==null? null : product.getSubCategory().getName())")

    @Mapping(target = "brandId",
            expression = "java(product.getBrand()==null? null : product.getBrand().getId())")
    @Mapping(target = "brandName",
            expression = "java(product.getBrand()==null? null : product.getBrand().getName())")

    @Mapping(target = "price", source = "price")
    @Mapping(target = "description", source = "description")

    @Mapping(target = "genders",
            expression = "java(product.getSubCategory()==null ? java.util.List.of() : product.getSubCategory().getGenders())")

    @Mapping(target = "colorAndSizes", expression = "java(mapColorAndSizes(product.getColorAndSizes()))")
    @Mapping(target = "offers", expression = "java(mapOffers(product.getProductOffers()))")
    @Mapping(target = "createdAt", source = "createdAt")
    ProductResponseDto toDto(Product product);


    default List<ColorAndSizeResponseDto> mapColorAndSizes(List<ColorAndSize> list) {
        if (list == null) return java.util.List.of();
        return list.stream().map(cs -> {
            ColorAndSizeResponseDto dto = new ColorAndSizeResponseDto();
            dto.setColor(cs.getColor() != null ? cs.getColor().name() : null);
            dto.setSizes(cs.getSizes());
            dto.setImageUrls(cs.getImageUrls());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
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
        if (list == null) return java.util.List.of();
        return list.stream()
                .map(o -> new ProductOfferResponseDto(
                        o.getId(),
                        o.getOfferType() == null ? null : o.getOfferType().name(),
                        o.getPrice(),
                        o.getProductCondition() == null ? null : o.getProductCondition().name(),
                        o.getRentDuration()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    default CategoryResponseDto mapCategoryToDto(Category category) {
        if (category == null) return null;
        return new CategoryResponseDto(category.getId(), category.getName());
    }
}
