package com.gym.security.controllers;

import com.gym.exceptions.EmptyUserListException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.exceptions.UserNotFoundException;
import com.gym.security.controllers.request.ChangePasswordDTO;
import com.gym.security.controllers.request.CreateUserDTO;
import com.gym.security.controllers.request.UpdateUserDTO;
import com.gym.security.controllers.response.ResponseUserDTO;
import com.gym.security.controllers.response.UserProfileDTO;
import com.gym.security.entities.UserEntity;
import com.gym.security.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-admin")
    @Operation(summary = "Crear usuario Administrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador creado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content =
                    @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametro",content =
            @Content),
    })
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
    @Operation(summary = "Crear usuario Cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametro",content =
            @Content),
    })
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
    @Operation(summary = "Modificar por nombre de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario modificado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",content =
            @Content),
    })
    public ResponseEntity<?> updateUser(@PathVariable String username, @Valid @RequestBody UpdateUserDTO updateUserDTO, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            ResponseUserDTO responseUserDTO = userService.updateUser(username, updateUserDTO, authorizationHeader);
            return ResponseEntity.ok(responseUserDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/get-all")
    @Operation(summary = "Obtener todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "204", description = "No content",content =
            @Content),
    })
    public ResponseEntity<List<ResponseUserDTO>> getAllUsers() {
        List<ResponseUserDTO> users;
        try {
            users = userService.getAllUsers();
        } catch (EmptyUserListException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",content =
            @Content),
    })
    public ResponseEntity<ResponseUserDTO> getUserById(@PathVariable Long id) {
        try {
            ResponseUserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/profile/{username}")
    @Operation(summary = "Obtener perfil del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil de usuario obtenido con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error de servidor",content =
            @Content),
            @ApiResponse(responseCode = "401", description = "Acceso no permitido",content =
            @Content),
            @ApiResponse(responseCode = "404", description = "Perfil de usuario no encontrado",content =
            @Content),
    })
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
    @Operation(summary = "Cambiar contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error de servidor",content =
            @Content),
            @ApiResponse(responseCode = "401", description = "Acceso no permitido",content =
            @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "Parametros incorrectos",content =
            @Content),
    })
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
    @Operation(summary = "Eliminar usuario cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<String> deleteUser(@RequestParam Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Se ha borrado el usuario con ID=" + id);
    }
}
