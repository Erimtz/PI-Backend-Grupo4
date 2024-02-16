package com.gym.controllers;

import com.gym.controllers.request.CreateUserDTO;
import com.gym.controllers.request.UpdateUserDTO;
import com.gym.entities.ERole;
import com.gym.entities.RoleEntity;
import com.gym.entities.UserEntity;
import com.gym.repositories.RoleRepository;
import com.gym.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PrincipalController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/hello")
    public String hello(){
        return "Hello World Not Secured";
    }

    @GetMapping("/helloSecured")
    public String helloSecured(){
        return "Hello World Secured";
    }
    @PostMapping("/createAdminUser")
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody CreateUserDTO createUserDTO){

//        Set<RoleEntity> roles = createUserDTO.getRoles().stream()
//                .map(role -> RoleEntity.builder()
//                        .name(ERole.valueOf(role))
//                        .build())
//                .collect(Collectors.toSet());
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya está en uso.");
        }

        // Verificar si el username ya existe en la base de datos
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            return ResponseEntity.badRequest().body("El username ya está en uso.");
        }

        Set<RoleEntity> roles = new HashSet<>();
        for (String roleName : createUserDTO.getRoles()) {
            RoleEntity roleEntity = roleRepository.findByName(ERole.valueOf(roleName))
                    .orElseGet(() -> roleRepository.save(new RoleEntity(null, ERole.valueOf(roleName))));
            roles.add(roleEntity);
        }

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .email(createUserDTO.getEmail())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(userEntity);
        return ResponseEntity.ok(userEntity);
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {

        // Crear un nuevo usuario con los atributos del DTO y el rol USER
        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .email(createUserDTO.getEmail())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .roles(Collections.singleton(
                        RoleEntity.builder()
                                .name(ERole.USER)
                                .build()))
                .build();

        userRepository.save(userEntity);
        return ResponseEntity.ok(userEntity);
    }

    @PutMapping("/updateUser/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
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

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String id){
        userRepository.deleteById(Long.parseLong(id));

        return "Se ha borrado el user con ID=".concat(id);
    }
}
