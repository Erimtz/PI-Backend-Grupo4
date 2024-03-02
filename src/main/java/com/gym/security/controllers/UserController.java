package com.gym.security.controllers;

import com.gym.exceptions.UnauthorizedException;
import com.gym.exceptions.UserNotFoundException;
import com.gym.security.controllers.request.ChangePasswordDTO;
import com.gym.security.controllers.request.CreateUserDTO;
import com.gym.security.controllers.request.UpdateUserDTO;
import com.gym.security.controllers.response.ResponseUserDTO;
import com.gym.security.controllers.response.UserProfileDTO;
import com.gym.security.entities.UserEntity;
import com.gym.security.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello World Not Secured";
    }

    @GetMapping("/helloSecured")
    public String helloSecured(){
        return "Hello World Secured";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        if (userService.existsByEmail(createUserDTO.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya está en uso.");
        }
        if (userService.existsByUsername(createUserDTO.getUsername())) {
            return ResponseEntity.badRequest().body("El username ya está en uso.");
        }
        UserEntity createdUser = userService.createAdminUser(createUserDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        if (userService.existsByEmail(createUserDTO.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya está en uso.");
        }
        if (userService.existsByUsername(createUserDTO.getUsername())) {
            return ResponseEntity.badRequest().body("El username ya está en uso.");
        }
        ResponseUserDTO responseUserDTO = userService.createUser(createUserDTO);
        return ResponseEntity.ok(responseUserDTO);
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @Valid @RequestBody UpdateUserDTO updateUserDTO, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            ResponseUserDTO responseUserDTO = userService.updateUser(username, updateUserDTO, authorizationHeader);
            return ResponseEntity.ok(responseUserDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username, HttpServletRequest request){
        try {
            String token = request.getHeader("Authorization");
            UserProfileDTO userProfileDTO = userService.getUserProfile(username, token);
            return ResponseEntity.ok(userProfileDTO);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la solicitud");
        }
    }

    @PutMapping("/update/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,
                                            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            userService.changePassword(changePasswordDTO, token);
            return ResponseEntity.ok("Contraseña actualizada exitosamente");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la solicitud");
        }
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestParam Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Se ha borrado el usuario con ID=" + id);
    }
}
