package com.dress.dressrenting.controller;

import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin-controller")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashBoardController {
    private final ProductService productService;

    @GetMapping("/all-products")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        return ResponseEntity.ok(productService.getPendingProducts());
    }

    @PostMapping("/approve-product")
    public ResponseEntity<List<ProductResponseDto>> approveProduct(String productCode) {
        return ResponseEntity.ok(productService.approveProduct(productCode));
    }

    @PostMapping("/disapprove-product")
    public ResponseEntity<ProductResponseDto> disapproveProduct(String productCode) {
        return ResponseEntity.ok(productService.disapproveProduct(productCode));
    }
}
