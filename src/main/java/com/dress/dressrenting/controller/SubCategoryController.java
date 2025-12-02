package com.dress.dressrenting.controller;

import com.dress.dressrenting.dto.request.SubCategoryRequestDto;
import com.dress.dressrenting.model.SubCategory;
import com.dress.dressrenting.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sub-categories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService service;

    @PostMapping
    public ResponseEntity<SubCategory> create(@RequestBody SubCategoryRequestDto subCategory) {
        return ResponseEntity.ok(service.create(subCategory));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategory> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SubCategory>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SubCategory>> getByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(service.getByCategoryId(categoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubCategory> update(@PathVariable Long id, @RequestBody SubCategoryRequestDto subCategory) {
        return ResponseEntity.ok(service.update(id, subCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
