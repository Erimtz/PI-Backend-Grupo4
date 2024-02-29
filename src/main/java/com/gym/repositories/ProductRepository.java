package com.gym.repositories;

import com.gym.entities.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    @Query("select p from Product p where p.category.id = ?1")
    List<Product> getProductsByCategory(Long category_id);
    List<Product> findByName(String name);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
}
