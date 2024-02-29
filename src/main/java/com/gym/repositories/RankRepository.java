package com.gym.repositories;

import com.gym.enums.ERank;
import com.gym.entities.Rank;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankRepository extends CrudRepository<Rank, Long> {
    Optional<Rank> findByName(ERank name);
}
