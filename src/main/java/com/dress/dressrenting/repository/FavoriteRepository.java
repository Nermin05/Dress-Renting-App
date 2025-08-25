package com.dress.dressrenting.repository;

import com.dress.dressrenting.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUser_IdAndProduct_ProductCode(Long userId, String productCode);

    List<Favorite> findByUser_Id(Long userId);

    void deleteByUser_IdAndProduct_ProductCode(Long userId, String productCode);
}
