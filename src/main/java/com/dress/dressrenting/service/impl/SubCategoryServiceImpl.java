package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.dto.request.SubCategoryRequestDto;
import com.dress.dressrenting.model.SubCategory;
import com.dress.dressrenting.repository.SubCategoryRepository;
import com.dress.dressrenting.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository repository;

    @Override
    public SubCategory create(SubCategoryRequestDto subCategoryRequestDto) {
        SubCategory subCategory=SubCategory.builder()
                .name(subCategoryRequestDto.name())
                .categoryId(subCategoryRequestDto.categoryId())
                .build();
        return repository.save(subCategory);
    }

    @Override
    public SubCategory update(Long id, SubCategoryRequestDto subCategory) {
        SubCategory existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found with id " + id));
        existing.setName(subCategory.name());
        existing.setCategoryId(subCategory.categoryId());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public SubCategory getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found with id " + id));
    }

    @Override
    public List<SubCategory> getAll() {
        return repository.findAll();
    }

    @Override
    public List<SubCategory> getByCategoryId(Long categoryId) {
        return repository.findByCategoryId(categoryId);
    }
}
