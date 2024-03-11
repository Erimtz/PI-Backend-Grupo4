//package com.gym.security.configuration.utils;
//
//import com.gym.enums.ERole;
//import com.gym.security.configuration.jwt.JwtUtils;
//import com.gym.security.entities.RoleEntity;
//import com.gym.security.entities.UserEntity;
//import com.gym.security.repositories.UserRepository;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//import java.util.Set;
//
//@Component
//public class AccessValidationUtils {
//
//    private final JwtUtils jwtUtils;
//    private final UserRepository userRepository;
//
//    @Autowired
//    public AccessValidationUtils(JwtUtils jwtUtils, UserRepository userRepository) {
//        this.jwtUtils = jwtUtils;
//        this.userRepository = userRepository;
//    }
//
//    public boolean canAccessAccount(Long requestedAccountId, String token) {
//        Long authenticatedUserId = getUserIdFromToken(token);
//        Optional<UserEntity> authenticatedUserOptional = userRepository.findById(authenticatedUserId);
//        if (authenticatedUserOptional.isPresent()) {
//            UserEntity authenticatedUser = authenticatedUserOptional.get();
//            // Verificar si el usuario autenticado es el propietario del recurso solicitado
//            if (authenticatedUser.getId().equals(requestedAccountId)) {
//                return true;
//            }
//            // Verificar si el usuario autenticado tiene el rol de ADMIN
//            Set<RoleEntity> roles = authenticatedUser.getRoles();
//            for (RoleEntity role : roles) {
//                if (role.getName() == ERole.ADMIN) {
//                    return true;
//                }
//            }
//        }
//        return false; // El usuario autenticado no existe en la base de datos o no tiene el rol de ADMIN
//    }
//
//    private Long getUserIdFromToken(String token) {
//        if (token != null && token.startsWith("Bearer ")) {
//            String tokenWithoutBearer = token.substring(7);
//            String username = jwtUtils.getUsernameFromToken(tokenWithoutBearer);
//            // Asumiendo que el nombre de usuario es el mismo que el ID en tu sistema
//            // Convertimos el nombre de usuario a Long
//            try {
//                return Long.parseLong(username);
//            } catch (NumberFormatException e) {
//                throw new IllegalArgumentException("Invalid user ID format in token");
//            }
//        } else {
//            throw new IllegalArgumentException("Invalid token format");
//        }
//    }
//}
