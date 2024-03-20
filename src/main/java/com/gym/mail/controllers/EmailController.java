package com.gym.mail.controllers;


import com.gym.dto.response.Message;
import com.gym.mail.domain.EmailValuesDTO;
import com.gym.mail.domain.EmailDTO;
import com.gym.mail.domain.EmailFileDTO;
import com.gym.mail.services.EmailService;
import com.gym.security.controllers.request.ResetPasswordDTO;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import com.gym.security.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recibir email")
    @PostMapping("/sendMessage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email recibido con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<?> receiveRequestEmail(@RequestBody EmailDTO emailDTO){
        System.out.println("Mensaje recibido"+ emailDTO);

        emailService.sendEmail(emailDTO.getToUsers(), emailDTO.getSubject(), emailDTO.getMessage());

        Map<String, String> response = new HashMap<>();
        response.put("estado", "Enviado");

        return ResponseEntity.ok(response);

    }

//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Recibir email con archivo")
    @PostMapping("/sendMessageFile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email con archivo recibido con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<?> receiveRequestEmailWithFile(@ModelAttribute EmailFileDTO emailFileDTO){
        try {
            String fileName = emailFileDTO.getFile().getOriginalFilename();
            Path path = Paths.get("src/main/resources/files/" + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(emailFileDTO.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            File file = path.toFile();

            emailService.sendEmailWithFile(emailFileDTO.getToUsers(), emailFileDTO.getSubject(), emailFileDTO.getMessage(), file);

            Map<String, String> response = new HashMap<>();
            response.put("estado", "Enviado");
            response.put("archivo", fileName);

            return ResponseEntity.ok(response);

        } catch (Exception e){
            throw new RuntimeException("Error al enviar el Email con el archivo. " + e.getMessage());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(summary = "Recuperar email")
    @PostMapping("/send-mail-recover")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo obtenido con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",content =
            @Content)
    })
    public ResponseEntity<?> sendEmailTemplate(@RequestBody EmailValuesDTO dto) {
        Optional<UserEntity> userOptional = userService.getByUsernameOrEmail(dto.getMailTo());
        if (!userOptional.isPresent())
            return new ResponseEntity<>(new Message("No existe ningún usuario con esas credenciales"), HttpStatus.NOT_FOUND);
        UserEntity user = userOptional.get();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(user.getEmail());
        dto.setSubject("cambio de contraseña");
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);
        user.setTokenPassword(tokenPassword);
        userRepository.save(user);
        emailService.sendEmailTemplate(dto);
        return new ResponseEntity<>("Te hemos enviado un correo", HttpStatus.OK);
    }

    @Operation(summary = "Cambio de contraseña")
    @PostMapping("/change-password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametros",content =
            @Content)
    })
    public ResponseEntity<?> changePassword(@Valid @RequestBody ResetPasswordDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return new ResponseEntity<>(new Message("Campos mal puestos"), HttpStatus.BAD_REQUEST);
        if(!dto.getPassword().equals(dto.getConfirmPassword()))
            return new ResponseEntity<>(new Message("Las contraseñas no coinciden"), HttpStatus.BAD_REQUEST);
        Optional<UserEntity> userOptional = userService.getByTokenPassword(dto.getTokenPassword());
        if (!userOptional.isPresent())
            return new ResponseEntity<>(new Message("No existe ningún usuario con esas credenciales"), HttpStatus.NOT_FOUND);
        UserEntity user = userOptional.get();
        String newPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(newPassword);
        user.setTokenPassword(null);
        userRepository.save(user);
        return new ResponseEntity<>(new Message("Contraseña actualizada"), HttpStatus.OK);
    }
}
