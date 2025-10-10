package com.dress.dressrenting.controller;

import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.request.UpdatedProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.enums.Color;
import com.dress.dressrenting.model.enums.Gender;
import com.dress.dressrenting.model.enums.OfferType;
import com.dress.dressrenting.model.enums.ProductCondition;
import com.dress.dressrenting.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all-products")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        return ResponseEntity.ok(productService.getApprovedProducts());
    }

    @GetMapping("/get-by-Offer-Type")
    public ResponseEntity<List<ProductResponseDto>> getAllByOfferType(@RequestParam OfferType offerType, @RequestParam ProductCondition productCondition) {
        return ResponseEntity.ok(productService.getAllByOfferType(offerType, productCondition));
    }

    @GetMapping("{productCode}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable String productCode) {
        return ResponseEntity.ok(productService.getById(productCode));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> save(
            @RequestPart("product") @Valid ProductRequestDto productRequestDto,
            @RequestParam MultiValueMap<String, MultipartFile> filesMap) {

        Map<String, List<MultipartFile>> colorImages = new HashMap<>();

        filesMap.forEach((key, value) -> {
            if (key.startsWith("images_")) {
                String colorKey = key.substring("images_".length()).toUpperCase();
                colorImages.put(colorKey, value);
            }
        });

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.save(productRequestDto, colorImages));
    }


    @PutMapping(value = "/{productCode}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable String productCode,
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        log.info("Received updated product JSON: {}", productJson);
        log.info("Received updated images count: {}", images != null ? images.size() : 0);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdatedProductRequestDto updatedProductRequestDto = objectMapper.readValue(productJson, UpdatedProductRequestDto.class);
            log.info("Parsed updated product: {}", updatedProductRequestDto);

            return ResponseEntity.ok(productService.update(productCode, updatedProductRequestDto, images));
        } catch (Exception e) {
            log.error("JSON parsing error during update: {}", e.getMessage());
            throw new RuntimeException("Updated product JSON parsing error", e);
        }
    }


    @DeleteMapping("{productCode}")
    public ResponseEntity<Void> delete(@PathVariable String productCode) {
        productService.delete(productCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponseDto>> filter(@RequestParam(required = false) Long subcategoryId,
                                                           @RequestParam(required = false) Color color,
                                                           @RequestParam(required = false) String size,
                                                           @RequestParam(required = false) Gender gender,
                                                           @RequestParam(required = false) BigDecimal minPrice,
                                                           @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(productService.filter(subcategoryId, color, size, gender, minPrice, maxPrice));
    }
}
