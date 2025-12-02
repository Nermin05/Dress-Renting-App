package com.dress.dressrenting.controller;

import com.dress.dressrenting.dto.request.BrandRequestDto;
import com.dress.dressrenting.model.Brand;
import com.dress.dressrenting.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService service;

    @PostMapping
    public ResponseEntity<Brand> create(@RequestBody BrandRequestDto brandRequestDto) {
        return ResponseEntity.ok(service.create(brandRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Brand>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> update(@PathVariable Long id, @RequestBody BrandRequestDto brandRequestDto) {
        return ResponseEntity.ok(service.update(id, brandRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
