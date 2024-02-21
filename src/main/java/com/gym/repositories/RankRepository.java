package com.gym.repositories;

import com.gym.entities.ERank;
import com.gym.entities.Rank;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RankRepository extends CrudRepository<Rank, Long> {
    Optional<Rank> findByName(ERank name);
}
