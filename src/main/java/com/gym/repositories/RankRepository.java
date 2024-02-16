package com.gym.repositories;

import com.gym.entities.Rank;
import org.springframework.data.repository.CrudRepository;

public interface RankRepository extends CrudRepository<Rank, Long> {
}
