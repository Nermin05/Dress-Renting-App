package com.dress.dressrenting.service;

import com.dress.dressrenting.dto.request.BrandRequestDto;
import com.dress.dressrenting.model.Brand;

import java.util.List;

public interface BrandService {
    Brand create(BrandRequestDto brandRequestDto);

    Brand update(Long id, BrandRequestDto brandRequestDto);

    void delete(Long id);

    Brand getById(Long id);

    List<Brand> getAll();
}
