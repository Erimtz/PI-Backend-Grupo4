package com.gym.service;


import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity findUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + username));

        return user;
    }

    public void deleteUserById(Long userId) {
        // Eliminar el usuario por su ID
        userRepository.deleteById(userId);
    }
}
