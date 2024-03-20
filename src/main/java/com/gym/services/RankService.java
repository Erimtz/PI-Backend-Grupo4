package com.gym.services;

import com.gym.entities.Rank;
import com.gym.enums.ERank;

import java.util.Optional;

public interface RankService {

    Optional<Rank> getRankByName(ERank name);

    Optional<Rank> getRankById(Long id); // Método agregado para obtener un rango por su ID
}
