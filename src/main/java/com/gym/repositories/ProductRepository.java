package com.gym.repositories;

import com.gym.entities.Account;
import com.gym.entities.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
