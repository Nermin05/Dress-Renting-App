package com.dress.dressrenting.controller;

import com.dress.dressrenting.dto.request.ColorAndSizeRequestDto;
import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.request.UpdatedProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.model.enums.Color;
import com.dress.dressrenting.model.enums.Gender;
import com.dress.dressrenting.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("{productCode}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable String productCode) {
        return ResponseEntity.ok(productService.getById(productCode));
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> save(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        log.info("Received product JSON: {}", productJson);
        log.info("Received images count: {}", images != null ? images.size() : 0);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductRequestDto productRequestDto = objectMapper.readValue(productJson, ProductRequestDto.class);
            log.info("Parsed product: {}", productRequestDto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productService.save(productRequestDto, images));
        } catch (Exception e) {
            log.error("JSON parsing error: {}", e.getMessage());
            throw new RuntimeException("Product JSON parsing error", e);
        }
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
