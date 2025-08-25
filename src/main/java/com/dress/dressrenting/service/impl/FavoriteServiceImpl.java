package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.exception.exceptions.NotFoundException;
import com.dress.dressrenting.mapper.ProductMapper;
import com.dress.dressrenting.model.CustomUserDetails;
import com.dress.dressrenting.model.Favorite;
import com.dress.dressrenting.model.Product;
import com.dress.dressrenting.model.User;
import com.dress.dressrenting.repository.FavoriteRepository;
import com.dress.dressrenting.repository.ProductRepository;
import com.dress.dressrenting.repository.UserRepository;
import com.dress.dressrenting.service.FavoriteService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, ProductMapper productMapper, ProductRepository productRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addFavorite(String productCode) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) throw new NotFoundException("User not found with id: " + currentUserId);
        User user = userRepository.findById(currentUserId).orElseThrow(() -> {
            log.error("User not found with id: {}", currentUserId);
            return new NotFoundException("User not found with id: " + currentUserId);
        });
        Product product = productRepository.findByProductCode(productCode).orElseThrow(() -> {
            log.error("Product not found with id: {}", productCode);
            return new NotFoundException("Product not found with id: " + productCode);
        });
        if (!favoriteRepository.existsByUser_IdAndProduct_ProductCode(currentUserId, productCode)) {
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .product(product)
                    .build();
            favoriteRepository.save(favorite);
        }
    }

    @Override
    public List<ProductResponseDto> getFavoriteList() {
        Long currentUserId = getCurrentUserId();
        List<Favorite> favorites = favoriteRepository.findByUser_Id(currentUserId);
        return favorites.stream().map(favorite -> productMapper.toDto(favorite.getProduct())).toList();
    }

    @Override
    @Transactional
    public void removeFromFavorite(String productCode) {
        Long currentUserId = getCurrentUserId();
        favoriteRepository.deleteByUser_IdAndProduct_ProductCode(currentUserId, productCode);
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new NotFoundException("User not found in security context: " + principal);
    }

}
