package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.dto.request.ProductFilterDto;
import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.request.UpdatedProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.exception.exceptions.NotFoundException;
import com.dress.dressrenting.mapper.ProductMapper;
import com.dress.dressrenting.model.ColorAndSize;
import com.dress.dressrenting.model.Product;
import com.dress.dressrenting.model.ProductOffer;
import com.dress.dressrenting.model.enums.*;
import com.dress.dressrenting.repository.ProductOfferRepository;
import com.dress.dressrenting.repository.ProductRepository;
import com.dress.dressrenting.repository.specification.ProductSpecification;
import com.dress.dressrenting.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CloudinaryService cloudinaryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductOfferRepository productOfferRepository;

    @Override
    public List<ProductResponseDto> getAll() {
        return productMapper.toDtoList(productRepository.findAll());
    }
    @Override
    public List<ProductResponseDto> getAllByOfferType(OfferType offerType, ProductCondition productCondition) {
        List<Product> products = productRepository.findAll().stream()
                .filter(product -> ProductStatus.ACTIVE.equals(product.getProductStatus()))
                .filter(product -> product.getProductOffers().stream()
                        .anyMatch(offer -> offer.getOfferType().equals(offerType)))
                .filter(product -> {
                    if (offerType == OfferType.SALE) {
                        return product.getProductOffers().stream()
                                .anyMatch(offer -> offer.getProductCondition() != null
                                        && offer.getProductCondition().equals(productCondition));
                    }
                    return true;
                })
                .toList();

        return productMapper.toDtoList(products);
    }



    @Override
    public ProductResponseDto getById(String productCode) {
        return productMapper.toDto(productRepository.findByProductCode(productCode).orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode)));
    }

    @Override
    public ProductResponseDto save(ProductRequestDto productRequestDto, List<MultipartFile> images) {
        Product product = productMapper.toEntity(productRequestDto);
        product = productRepository.save(product);

        Product finalProduct = product;

        List<ColorAndSize> colorAndSizes = productRequestDto.getColorAndSizes()
                .stream()
                .map(colorDto -> {
                    ColorAndSize colorAndSize = productMapper.toColorAndSize(colorDto);

                    List<String> urls = (images != null ? images : Collections.emptyList())
                            .stream()
                            .map(file -> {
                                try {
                                    return cloudinaryService.upload((MultipartFile) file).get("url").toString();
                                } catch (IOException e) {
                                    throw new RuntimeException("Image upload failed", e);
                                }
                            })
                            .collect(Collectors.toList());

                    colorAndSize.setImageUrls(urls);
                    colorAndSize.setPhotoCount(urls.size());
                    colorAndSize.setProduct(finalProduct);

                    return colorAndSize;
                })
                .collect(Collectors.toList());

        if (product.getColorAndSizes() == null) {
            product.setColorAndSizes(new ArrayList<>());
        } else {
            product.getColorAndSizes().clear();
        }
        product.getColorAndSizes().addAll(colorAndSizes);

        if (product.getProductCode() == null) {
            product.setProductCode(String.format("%04d", product.getId()));
            product = productRepository.save(product);
        }

        Product finalProduct1 = product;

        List<ProductOffer> offers = productRequestDto.getProductOffers()
                .stream()
                .map(dto -> {
                    if (dto.offerType() == OfferType.RENT && dto.rentDuration() == null) {
                        throw new IllegalArgumentException("Rent offer must have rentDuration!");
                    }

                    BigDecimal totalPrice = dto.offerType() == OfferType.RENT
                            ? dto.price().multiply(BigDecimal.valueOf(dto.rentDuration()))
                            : dto.price();

                    if (dto.offerType() == OfferType.SALE && dto.condition() == null) {
                        throw new IllegalArgumentException("SALE offers must have condition: FIRST_HAND or SECOND_HAND");
                    }

                    if (dto.offerType() == OfferType.SALE && dto.condition() == ProductCondition.SECOND_HAND) {
                        totalPrice = totalPrice.multiply(BigDecimal.valueOf(0.8));
                    }

                    return ProductOffer.builder()
                            .product(finalProduct1)
                            .offerType(dto.offerType())
                            .price(totalPrice)
                            .rentDuration(dto.rentDuration())
                            .productCondition(dto.condition())
                            .build();
                })
                .collect(Collectors.toList());

        if (product.getProductOffers() == null) {
            product.setProductOffers(new ArrayList<>());
        } else {
            product.getProductOffers().clear();
        }
        product.getProductOffers().addAll(offers);
        productOfferRepository.saveAll(offers);

        BigDecimal productPrice = offers.stream()
                .map(ProductOffer::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        product.setPrice(productPrice);
        product = productRepository.save(product);

        return productMapper.toDto(product);
    }


    @Transactional
    @Override
    public ProductResponseDto update(String productCode, UpdatedProductRequestDto requestDto, List<MultipartFile> images) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode));

        product.setPrice(requestDto.price());
        product.setGender(requestDto.gender());

        List<ColorAndSize> updatedColorAndSizes = product.getColorAndSizes().stream()
                .map(cs -> {
                    List<String> kept = cs.getImageUrls().stream()
                            .filter(url -> requestDto.keptImageUrls().contains(url))
                            .toList();
                    cs.setImageUrls(kept);
                    cs.setPhotoCount(kept.size());
                    return cs;
                })
                .toList();

        if (images != null && !images.isEmpty()) {
            ColorAndSize newCs = ColorAndSize.builder()
                    .color(requestDto.color())
                    .product(product)
                    .build();
            List<String> newUrls = images.stream().map(file -> {
                try {
                    return cloudinaryService.upload(file).get("url").toString();
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed", e);
                }
            }).toList();
            newCs.setImageUrls(newUrls);
            newCs.setPhotoCount(newUrls.size());
            updatedColorAndSizes.add(newCs);
        }
        product.setColorAndSizes(updatedColorAndSizes);

        productOfferRepository.deleteAll(product.getProductOffers());

        List<ProductOffer> newOffers = requestDto.productOffers().stream().map(dto -> {
            if (dto.offerType() == OfferType.RENT && dto.rentDuration() == null) {
                throw new IllegalArgumentException("Rent offer must have rentDuration!");
            }
            BigDecimal totalPrice = dto.offerType() == OfferType.RENT
                    ? dto.price().multiply(BigDecimal.valueOf(dto.rentDuration()))
                    : dto.price();

            return ProductOffer.builder()
                    .product(product)
                    .offerType(dto.offerType())
                    .price(totalPrice)
                    .rentDuration(dto.rentDuration())
                    .build();
        }).toList();

        productOfferRepository.saveAll(newOffers);
        product.setProductOffers(newOffers);

        return productMapper.toDto(productRepository.save(product));
    }


    private String getIdFromUrl(String url) {
        String[] split = url.split("/");
        String fileName = split[split.length - 1];
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    @Override
    @Transactional
    public void delete(String productCode) {
        Product product = productRepository.findByProductCode(productCode).orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode));
        if (product.getColorAndSizes() != null) {
            product.getColorAndSizes().forEach(cs -> {
                if (cs.getImageUrls() != null) {
                    cs.getImageUrls().forEach(url -> {
                        try {
                            String publicId = getIdFromUrl(url);
                            cloudinaryService.delete(publicId);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        }
        productRepository.deleteByProductCode(productCode);
    }

    @Override
    public List<ProductResponseDto> filter(Long subcategoryId, Color color, String size, Gender gender, BigDecimal minPrice, BigDecimal maxPrice) {
        ProductFilterDto productFilterDto = new ProductFilterDto(subcategoryId, size, gender, color, minPrice, maxPrice);
        List<Product> filtered = productRepository.findAll(ProductSpecification.filter(productFilterDto));
        return productMapper.toDtoList(filtered);
    }

    @Override
    public void changeStatus(String productCode, ProductStatus productStatus) {
        Product product = findByProductCode(productCode);
        product.setProductStatus(productStatus);
        productRepository.save(product);
    }

    @Override
    public List<ProductResponseDto> approveProduct(String productCode) {
        Product product = findByProductCode(productCode);
        product.setProductStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
        return productMapper.toDtoList(productRepository.findByProductStatus(ProductStatus.ACTIVE));
    }

    @Override
    public List<ProductResponseDto> disapproveProduct(String productCode) {
        Product product = findByProductCode(productCode);
        product.setProductStatus(ProductStatus.DELETED);
        productRepository.save(product);
        return productMapper.toDtoList(productRepository.findByProductStatus(ProductStatus.ACTIVE));
    }

    private Product findByProductCode(String productCode) {
        return productRepository.findByProductCode(productCode).orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode));
    }
}
