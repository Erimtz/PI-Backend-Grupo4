package com.gym.repositories;

import com.gym.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u from UserEntity u WHERE u.username = ?1")
    Optional<UserEntity> getName(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}