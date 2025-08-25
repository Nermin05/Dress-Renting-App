package com.dress.dressrenting.service;

import com.dress.dressrenting.dto.response.ProductResponseDto;

import java.util.List;

public interface FavoriteService {
    void addFavorite(String productCode);

    List<ProductResponseDto> getFavoriteList();

    void removeFromFavorite(String productCode);
}
