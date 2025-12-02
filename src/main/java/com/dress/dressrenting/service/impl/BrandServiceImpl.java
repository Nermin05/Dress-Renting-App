package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.dto.request.BrandRequestDto;
import com.dress.dressrenting.model.Brand;
import com.dress.dressrenting.repository.BrandRepository;
import com.dress.dressrenting.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public Brand create(BrandRequestDto brandRequestDto) {
        Brand brand = Brand.builder()
                .name(brandRequestDto.name())
                .build();
        return brandRepository.save(brand);
    }

    @Override
    public Brand update(Long id, BrandRequestDto brandRequestDto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id " + id));
        brand.setName(brandRequestDto.name());
        return brandRepository.save(brand);
    }

    @Override
    public void delete(Long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public Brand getById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id " + id));
    }

    @Override
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

}
