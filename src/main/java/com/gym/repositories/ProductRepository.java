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
            "AND (:hasStock IS NULL OR (:hasStock = true AND p.stock > 0) OR (:hasStock = false AND p.stock <= 0)) " +
            "ORDER BY " +
            "CASE WHEN :orderBy = 'price' AND :orderDirection = 'asc' THEN p.price END ASC, " +
            "CASE WHEN :orderBy = 'price' AND :orderDirection = 'desc' THEN p.price END DESC, " +
            "CASE WHEN :orderBy = 'name' AND :orderDirection = 'asc' THEN p.name END ASC, " +
            "CASE WHEN :orderBy = 'name' AND :orderDirection = 'desc' THEN p.name END DESC")
    List<Product> findProductsByCategoryAndFilters(
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Boolean hasStock,
            String orderBy,
            String orderDirection
    );
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> findProductsByName(@Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " + // Búsqueda por nombre
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " + // Filtro de precio mínimo
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " + // Filtro de precio máximo
            "AND (:hasStock IS NULL OR (:hasStock = true AND p.stock > 0) OR (:hasStock = false AND p.stock <= 0)) " + // Filtro de stock
            "ORDER BY " +
            "CASE WHEN :orderBy = 'price' AND :orderDirection = 'asc' THEN p.price END ASC, " + // Ordenamiento por precio ascendente
            "CASE WHEN :orderBy = 'price' AND :orderDirection = 'desc' THEN p.price END DESC, " + // Ordenamiento por precio descendente
            "CASE WHEN :orderBy = 'name' AND :orderDirection = 'asc' THEN p.name END ASC, " + // Ordenamiento por nombre ascendente
            "CASE WHEN :orderBy = 'name' AND :orderDirection = 'desc' THEN p.name END DESC") // Ordenamiento por nombre descendente
    List<Product> findProductsByNameAndFilters(
            @Param("searchTerm") String searchTerm,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("hasStock") Boolean hasStock,
            @Param("orderBy") String orderBy,
            @Param("orderDirection") String orderDirection
    );
}
