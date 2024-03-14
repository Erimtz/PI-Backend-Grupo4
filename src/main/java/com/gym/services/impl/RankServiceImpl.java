package com.gym.services.impl;

import com.gym.entities.Rank;
import com.gym.enums.ERank;
import com.gym.repositories.RankRepository;
import com.gym.services.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RankServiceImpl implements RankService {

    private final RankRepository rankRepository;

    @Autowired
    public RankServiceImpl(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    @Override
    public Optional<Rank> getRankByName(ERank name) {
        return rankRepository.findByName(name);
    }

    @Override
    public Optional<Rank> getRankById(Long id) {
        return rankRepository.findById(id);
    }
}
