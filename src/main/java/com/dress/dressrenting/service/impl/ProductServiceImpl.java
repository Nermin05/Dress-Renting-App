package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.dto.request.ProductFilterDto;
import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.request.UpdatedProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.exception.exceptions.NotFoundException;
import com.dress.dressrenting.mapper.ProductMapper;
import com.dress.dressrenting.model.ColorAndSize;
import com.dress.dressrenting.model.Product;
import com.dress.dressrenting.model.enums.Color;
import com.dress.dressrenting.model.enums.Gender;
import com.dress.dressrenting.model.enums.ProductStatus;
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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CloudinaryService cloudinaryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDto> getAll() {
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Override
    public List<ProductResponseDto> getAllActive() {
        return productMapper.toDtoList(productRepository.findByProductStatus(ProductStatus.ACTIVE));
    }

    @Override
    public ProductResponseDto getById(String productCode) {
        return productMapper.toDto(productRepository.findByProductCode(productCode).orElseThrow(() -> {
            log.error("Product not found with id: {}", productCode);
            return new NotFoundException("Product not found with id: " + productCode);
        }));
    }

    @Override
    public ProductResponseDto save(ProductRequestDto productRequestDto, List<MultipartFile> images) {
        Product product = productMapper.toEntity(productRequestDto);
        List<ColorAndSize> colorAndSizes = productRequestDto.getColorAndSizes().stream().map(colorDto -> {
            ColorAndSize colorAndSize = productMapper.toColorAndSize(colorDto);
            List<String> urls = images.stream().map(file -> {
                try {
                    return cloudinaryService.upload(file).get("url").toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
            colorAndSize.setImageUrls(urls);
            colorAndSize.setPhotoCount(urls.size());
            return colorAndSize;
        }).toList();
        product.setColorAndSizes(colorAndSizes);
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    @Override
    public ProductResponseDto update(String productCode, UpdatedProductRequestDto requestDto, List<MultipartFile> images) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode));

        product.setPrice(requestDto.price());
        product.setGender(requestDto.gender());

        List<ColorAndSize> updatedColorAndSizes = product.getColorAndSizes().stream().peek(cs -> {
            List<String> keptImages = cs.getImageUrls().stream()
                    .filter(url -> requestDto.keptImageIds().contains(getIdFromUrl(url)))
                    .toList();

            cs.setImageUrls(keptImages);
            cs.setPhotoCount(keptImages.size());
        }).toList();

        if (!images.isEmpty()) {
            ColorAndSize newCs = ColorAndSize.builder()
                    .color(requestDto.color())
                    .product(product)
                    .build();

            List<String> newUrls = images.stream().map(file -> {
                try {
                    return cloudinaryService.upload(file).get("url").toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            newCs.setImageUrls(newUrls);
            newCs.setPhotoCount(newUrls.size());

            updatedColorAndSizes.add(newCs);
        }

        product.setColorAndSizes(updatedColorAndSizes);
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
        Product product = productRepository.findByProductCode(productCode).orElseThrow(() -> {
            log.error("Product not found with id: {}", productCode);
            return new NotFoundException("Product not found with id: " + productCode);
        });
        if (product.getColorAndSizes() != null) {
            product.getColorAndSizes().forEach(cs -> {
                if (cs.getImageUrls() != null) {
                    cs.getImageUrls().stream().forEach(url -> {
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
        log.info("Product deleted with id: {}", productCode);
    }

    @Override
    public List<ProductResponseDto> filter(Long subcategoryId,
                                           Color color,
                                           String size,
                                           Gender gender,
                                           BigDecimal minPrice,
                                           BigDecimal maxPrice) {
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
        return productRepository.findByProductCode(productCode).orElseThrow(() -> {
            log.error("Product not found with id: {}", productCode);
            return new NotFoundException("Product not found with id: " + productCode);
        });
    }
}
