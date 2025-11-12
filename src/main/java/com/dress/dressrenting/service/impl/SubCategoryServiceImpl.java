//package com.dress.dressrenting.service.impl;
//
//import com.dress.dressrenting.dto.request.SubCategoryRequestDto;
//import com.dress.dressrenting.exception.exceptions.NotFoundException;
//import com.dress.dressrenting.model.Category;
//import com.dress.dressrenting.model.SubCategory;
//import com.dress.dressrenting.repository.CategoryRepository;
//import com.dress.dressrenting.repository.SubCategoryRepository;
//import com.dress.dressrenting.service.SubCategoryService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class SubCategoryServiceImpl implements SubCategoryService {
//
//    private final SubCategoryRepository repository;
//    private final CategoryRepository categoryRepository;
//
//    @Override
//    public SubCategory create(SubCategoryRequestDto subCategoryRequestDto) {
//        Category category = categoryRepository.findById(subCategoryRequestDto.categoryId()).orElseThrow(() -> {
//            log.error("Category not found with id: {}", subCategoryRequestDto.categoryId());
//            return new NotFoundException("Category not found with id: " + subCategoryRequestDto.categoryId());
//        });
//        SubCategory subCategory = SubCategory.builder()
//                .name(subCategoryRequestDto.name())
//                .category(category)
//                .genders(subCategoryRequestDto.genders())
//                .build();
//        return repository.save(subCategory);
//    }
//
//    @Override
//    public SubCategory update(Long id, SubCategoryRequestDto subCategory) {
//        Category category = categoryRepository.findById(subCategory.categoryId()).orElseThrow(() -> {
//            log.error("Category not found with id: {}", subCategory.categoryId());
//            return new NotFoundException("Category not found with id: " + subCategory.categoryId());
//        });
//        SubCategory existing = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("SubCategory not found with id " + id));
//        existing.setName(subCategory.name());
//        existing.setCategory(category);
//        existing.setGenders(subCategory.genders());
//        return repository.save(existing);
//    }
//
//    @Override
//    public void delete(Long id) {
//        repository.deleteById(id);
//    }
//
//    @Override
//    public SubCategory getById(Long id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("SubCategory not found with id " + id));
//    }
//
//    @Override
//    public List<SubCategory> getAll() {
//        return repository.findAll();
//    }
//
//    @Override
//    public List<SubCategory> getByCategoryId(Long categoryId) {
//        return repository.findByCategoryId(categoryId);
//    }
//}
