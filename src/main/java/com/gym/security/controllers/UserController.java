package com.gym.security.controllers;

import com.gym.exceptions.UserAlreadyExistsException;
import com.gym.security.configuration.jwt.JwtUtils;
import com.gym.security.controllers.request.ChangePasswordDTO;
import com.gym.security.controllers.request.CreateUserDTO;
import com.gym.security.controllers.request.UpdateUserDTO;
import com.gym.security.controllers.request.UserProfileDTO;
import com.gym.security.enums.ERole;
import com.gym.security.entities.RoleEntity;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.RoleRepository;
import com.gym.security.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-admin-user")
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody CreateUserDTO createUserDTO){

        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya está en uso.");
        }

        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            return ResponseEntity.badRequest().body("El username ya está en uso.");
        }

//        Set<RoleEntity> roles = createUserDTO.getRoles().stream()
//                .map(role -> RoleEntity.builder()
//                        .name(ERole.valueOf(role))
//                        .build())
//                .collect(Collectors.toSet());

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

        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new UserAlreadyExistsException("El usuario ya existe con el nombre de usuario: " + createUserDTO.getUsername());
        }
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new UserAlreadyExistsException("El usuario ya existe con el correo electrónico: " + createUserDTO.getEmail());
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

        Optional<RoleEntity> userRoleOptional = roleRepository.findByName(ERole.USER);

        RoleEntity userRole = userRoleOptional.orElseGet(() -> {
            RoleEntity newUserRole = RoleEntity.builder()
                    .name(ERole.USER)
                    .build();
            return roleRepository.save(newUserRole);
        });

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .email(createUserDTO.getEmail())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .roles(Collections.singleton(userRole))
                .build();

//        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
//            throw new UserAlreadyExistsException("El usuario ya existe con el nombre de usuario: " + createUserDTO.getUsername());
//        }
//        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
//            throw new UserAlreadyExistsException("El usuario ya existe con el correo electrónico: " + createUserDTO.getEmail());
//        }

        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya está en uso.");
        }

        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            return ResponseEntity.badRequest().body("El username ya está en uso.");
        }

//        UserEntity userEntity = UserEntity.builder()
//                .username(createUserDTO.getUsername())
//                .firstName(createUserDTO.getFirstName())
//                .lastName(createUserDTO.getLastName())
//                .email(createUserDTO.getEmail())
//                .password(passwordEncoder.encode(createUserDTO.getPassword()))
//                .roles(Collections.singleton(
//                        RoleEntity.builder()
//                                .name(ERole.USER)
//                                .build()))
//                .build();

        userRepository.save(userEntity);
        return ResponseEntity.ok(userEntity);
    }

    @PutMapping("/update/{username}")
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
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se encontró un token de autorización válido.");
        }
        token = token.substring(7);

        String username = jwtUtils.getUsernameFromToken(token);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo obtener el nombre de usuario del token.");
        }

        String currentPassword = changePasswordDTO.getCurrentPassword();
        String newPassword = changePasswordDTO.getNewPassword();
        String confirmPassword = changePasswordDTO.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("La nueva contraseña y la confirmación de la contraseña no coinciden");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("La contraseña actual es incorrecta");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }

    @DeleteMapping("/delete-user")
    public String deleteUser(@RequestParam String id){
        userRepository.deleteById(Long.parseLong(id));

        return "Se ha borrado el user con ID=".concat(id);
    }

//    @GetMapping("/profile")
//    public ResponseEntity<UserProfileDTO> showProfile(@AuthenticationPrincipal UserDetails userDetails) {
//
//        String username = userDetails.getUsername();
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//
//        UserEntity userEntity = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//
//        UserProfileDTO userProfileDTO = new UserProfileDTO(
//                username,
//                userEntity.getFirstName(),
//                userEntity.getLastName(),
//                userEntity.getEmail(),
//                roles
//        );
//
//        return ResponseEntity.ok(userProfileDTO);
//    }
}
