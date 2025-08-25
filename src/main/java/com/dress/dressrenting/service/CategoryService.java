package com.dress.dressrenting.service;

import com.dress.dressrenting.dto.request.CategoryRequestDto;
import com.dress.dressrenting.model.Category;

import java.util.List;

public interface CategoryService {
    Category create(CategoryRequestDto category);

    Category update(Long id, CategoryRequestDto category);

    void delete(Long id);

    Category getById(Long id);

    List<Category> getAll();
}
