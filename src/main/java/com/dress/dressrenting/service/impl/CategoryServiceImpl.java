package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.dto.request.CategoryRequestDto;
import com.dress.dressrenting.model.Category;
import com.dress.dressrenting.repository.CategoryRepository;
import com.dress.dressrenting.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public Category create(CategoryRequestDto categoryRequestDto) {
        Category category = Category.builder()
                .name(categoryRequestDto.name())
                .build();
        return repository.save(category);
    }

    @Override
    public Category update(Long id, CategoryRequestDto categoryRequestDto) {
        Category existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
        existing.setName(categoryRequestDto.name());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Category getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    @Override
    public List<Category> getAll() {
        return repository.findAll();
    }
}
