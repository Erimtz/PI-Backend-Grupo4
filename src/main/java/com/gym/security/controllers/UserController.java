package com.gym.security.controllers;

import com.gym.security.controllers.request.ChangePasswordDTO;
import com.gym.security.controllers.request.CreateUserDTO;
import com.gym.security.controllers.request.UpdateUserDTO;
import com.gym.security.enums.ERole;
import com.gym.security.entities.RoleEntity;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.RoleRepository;
import com.gym.security.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

//    @GetMapping("/hello")
//    public String hello(){
//        return "Hello World Not Secured";
//    }
//
//    @GetMapping("/helloSecured")
//    public String helloSecured(){
//        return "Hello World Secured";
//    }
    @PostMapping("/create-admin-user")
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody CreateUserDTO createUserDTO){

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

    @PostMapping("/create-user")
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

    @PutMapping("/update-user/{username}")
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

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody ChangePasswordDTO changePasswordDTO) {

        // Obtener los datos del objeto de solicitud
        String currentPassword = changePasswordDTO.getCurrentPassword();
        String newPassword = changePasswordDTO.getNewPassword();
        String confirmPassword = changePasswordDTO.getConfirmPassword();

        // Verificar si la nueva contraseña y la confirmación de la contraseña son iguales
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("La nueva contraseña y la confirmación de la contraseña no coinciden");
        }

        // Obtener el nombre de usuario del UserDetails
        String username = userDetails.getUsername();

        // Obtener el usuario desde el repositorio
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si la contraseña actual proporcionada coincide con la contraseña almacenada
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("La contraseña actual es incorrecta");
        }

        // Codificar la nueva contraseña
        String encodedPassword = passwordEncoder.encode(newPassword);

        // Actualizar la contraseña del usuario
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }

    @DeleteMapping("/delete-user")
    public String deleteUser(@RequestParam String id){
        userRepository.deleteById(Long.parseLong(id));

        return "Se ha borrado el user con ID=".concat(id);
    }
}
