package com.gym.security.services;//package com.gym.security.services;

import com.gym.exceptions.EmailAlreadyExistsException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.exceptions.UserNotFoundException;
import com.gym.exceptions.UsernameAlreadyExistsException;
import com.gym.security.configuration.jwt.JwtUtils;
import com.gym.security.controllers.request.ChangePasswordDTO;
import com.gym.security.controllers.request.CreateUserDTO;
import com.gym.security.controllers.request.UpdateUserDTO;
import com.gym.security.entities.RoleEntity;
import com.gym.security.entities.UserEntity;
import com.gym.security.enums.ERole;
import com.gym.security.repositories.RoleRepository;
import com.gym.security.repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public UserEntity createAdminUser(CreateUserDTO createUserDTO) {

        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new EmailAlreadyExistsException("El email ya está en uso.");
        }

        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("El username ya está en uso.");
        }

        Set<RoleEntity> roles = new HashSet<>();
        for (String roleName : createUserDTO.getRoles()) {
            RoleEntity role = roleRepository.findByName(ERole.valueOf(roleName))
                    .orElseGet(() -> {
                        RoleEntity newRole = RoleEntity.builder()
                                .name(ERole.valueOf(roleName))
                                .build();
                        return roleRepository.save(newRole);
                    });
            roles.add(role);
        }

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .email(createUserDTO.getEmail())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .roles(roles)
                .build();

        return userRepository.save(userEntity);
    }

    public UserEntity createUser(CreateUserDTO createUserDTO) {
        Optional<RoleEntity> userRoleOptional = roleRepository.findByName(ERole.USER);
        RoleEntity userRole = userRoleOptional.orElseGet(() -> {
            RoleEntity newUserRole = RoleEntity.builder()
                    .name(ERole.USER)
                    .build();
            return roleRepository.save(newUserRole);
        });

        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new EmailAlreadyExistsException("El email ya está en uso.");
        }

        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("El username ya está en uso.");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .email(createUserDTO.getEmail())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .roles(Collections.singleton(userRole))
                .build();

        return userRepository.save(userEntity);
    }

    public UserEntity updateUser(String username, UpdateUserDTO updateUserDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado");
        }

        UserEntity user = optionalUser.get();

        if (updateUserDTO.getEmail() != null) {
            user.setEmail(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getFirstName() != null) {
            user.setFirstName(updateUserDTO.getFirstName());
        }
        if (updateUserDTO.getLastName() != null) {
            user.setLastName(updateUserDTO.getLastName());
        }

        return userRepository.save(user);
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO, String token) throws BadRequestException {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("No se encontró un token de autorización válido.");
        }
        token = token.substring(7);

        String username = jwtUtils.getUsernameFromToken(token);

        if (username == null) {
            throw new UnauthorizedException("No se pudo obtener el nombre de usuario del token.");
        }

        String currentPassword = changePasswordDTO.getCurrentPassword();
        String newPassword = changePasswordDTO.getNewPassword();
        String confirmPassword = changePasswordDTO.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("La nueva contraseña y la confirmación de la contraseña no coinciden");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("El usuario con ID=" + id + " no existe.");
        }
        userRepository.deleteById(id);
    }
}