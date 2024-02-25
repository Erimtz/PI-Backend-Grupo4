package com.gym.repositories;

import com.gym.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByAccountId(Long id);

    @Query("SELECT s FROM Subscription s WHERE s.endDate < CURRENT_DATE")
    List<Subscription> findExpiredSubscriptions();

    @Query("SELECT s FROM Subscription s WHERE s.endDate >= CURRENT_DATE")
    List<Subscription> findActiveSubscriptions();
}
