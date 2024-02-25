package com.gym.repositories;

import com.gym.entities.StoreSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreSubscriptionRepository extends JpaRepository<StoreSubscription, Long> {
}
