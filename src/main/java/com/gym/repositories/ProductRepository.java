package com.gym.repositories;

import com.gym.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.category.id = ?1")
    List<Product> getProductsByCategory(Long category_id);
    List<Product> findByName(String name);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :productId")
    Optional<Product> findByIdWithImages(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p WHERE " +
            "p.category.id = :categoryId " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:hasStock IS NULL OR (:hasStock = true AND p.stock > 0) OR (:hasStock = false AND p.stock <= 0))")
    List<Product> findProductsByCategoryAndFilters(Long categoryId, Double minPrice, Double maxPrice, Boolean hasStock);
}
