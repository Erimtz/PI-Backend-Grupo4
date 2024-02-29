package com.gym.repositories;

import com.gym.entities.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);

    Optional<Account> findByUserUsername(String username);
}