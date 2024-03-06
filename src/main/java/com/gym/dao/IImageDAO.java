package com.gym.dao;

import com.gym.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IImageDAO extends JpaRepository<Image, Long> {
}
