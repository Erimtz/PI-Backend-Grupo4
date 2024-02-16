package com.gym.repositories;

import com.gym.entities.Account;
import com.gym.entities.Purchase;
import org.springframework.data.repository.CrudRepository;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {
}
