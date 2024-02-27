package com.gym.security.services;//package com.gym.security.services;

import com.gym.dto.Message;
import com.gym.dto.ResponseCouponDTO;
import com.gym.entities.*;
import com.gym.exceptions.*;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.RankRepository;
import com.gym.repositories.SubscriptionRepository;
import com.gym.security.configuration.jwt.JwtUtils;
import com.gym.security.controllers.request.ChangePasswordDTO;
import com.gym.security.controllers.request.CreateUserDTO;
import com.gym.security.controllers.request.UpdateUserDTO;
import com.gym.security.controllers.response.ResponseUserDTO;
import com.gym.security.controllers.response.UserProfileDTO;
import com.gym.security.entities.RoleEntity;
import com.gym.security.entities.UserEntity;
import com.gym.security.enums.ERole;
import com.gym.security.repositories.RoleRepository;
import com.gym.security.repositories.UserRepository;
import com.gym.services.AccountService;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service

public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final RankRepository rankRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, AccountRepository accountRepository,
                       AccountService accountService, RankRepository rankRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.rankRepository = rankRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.jwtUtils = jwtUtils;
    }

    //    @GetMapping("/profile/{username}")
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

    public UserProfileDTO getUserProfile(String username, String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("No se encontró un token de autorización válido.");
        }
        token = token.substring(7);

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (username == null) {
            throw new UnauthorizedException("No se pudo obtener el nombre de usuario del token.");
        } else if (!username.equals(tokenUsername)) {
            throw new UnauthorizedException("El usuario a acceder no coincide con el usuario autenticado");
        }

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado");
        }
        UserEntity user = optionalUser.get();
        Account account = accountRepository.findByUserId(optionalUser.get().getId()).orElse(null);
        Subscription subscription = subscriptionRepository.findByAccountId(account.getId()).orElse(null);
        UserProfileDTO userProfileDTO = convertProfileDTO(subscription);
        return userProfileDTO;
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

    @Transactional
    public ResponseUserDTO createUser(CreateUserDTO createUserDTO) {
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

        userRepository.save(userEntity);

        Optional<Rank> accountRankOptional = rankRepository.findByName(ERank.BRONZE);

        Rank rankAccount = accountRankOptional.orElseGet(() -> {
            Rank newAccountRank = Rank.builder()
                    .name(ERank.BRONZE)
                    .build();
            return rankRepository.save(newAccountRank);
        });
        Account account = Account.builder()
                .user(userEntity)
                .transferList(new ArrayList<>())
                .couponList(new ArrayList<>())
                .purchaseList(new ArrayList<>())
                .creditBalance(BigDecimal.valueOf(0.0))
                .rank(rankAccount)
                .document(createUserDTO.getDocument())
                .build();
        Account savedAccount = accountService.createAccount(userEntity);

        ResponseUserDTO responseUserDTO = convertToDto(savedAccount);

        return responseUserDTO;
    }

    public ResponseUserDTO updateUser(String username, UpdateUserDTO updateUserDTO, String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("No se encontró un token de autorización válido.");
        }
        token = token.substring(7);

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (username == null) {
            throw new UnauthorizedException("No se pudo obtener el nombre de usuario del token.");
        } else if (!username.equals(tokenUsername)) {
            throw new UnauthorizedException("El usuario a modificar no coincide con el usuario autenticado");
        }

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


        UserEntity updatedUser = userRepository.save(user);
        Account userAccount = accountRepository.findByUserId(updatedUser.getId()).orElse(null);
        ResponseUserDTO responseUserDTO = convertToDto(userAccount);
        return responseUserDTO;
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
        Account account = accountRepository.findByUserId(id).get();
        subscriptionRepository.deleteById(account.getId());
        accountRepository.deleteById(account.getId());
        userRepository.deleteById(id);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Optional<UserEntity> getByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> getByUsernameOrEmail(String usernameOrEmail){
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public Optional<UserEntity> getByTokenPassword(String tokenPassword){
        return userRepository.findByTokenPassword(tokenPassword);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    public Message save(CreateUserDTO createUserDTO){
        if(userRepository.existsByUsername(createUserDTO.getUsername()))
            throw new CustomException(HttpStatus.BAD_REQUEST, "ese nombre de usuario ya existe");
        if(userRepository.existsByEmail(createUserDTO.getEmail()))
            throw new CustomException(HttpStatus.BAD_REQUEST, "ese email de usuario ya existe");
        UserEntity user =
                new UserEntity(createUserDTO.getFirstName(), createUserDTO.getLastName(), createUserDTO.getUsername(), createUserDTO.getEmail(),
                        passwordEncoder.encode(createUserDTO.getPassword()));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.USER).get());
        user.setRoles(roles);
        userRepository.save(user);
        return new Message(user.getUsername() + " ha sido creado");
    }

//    @GetMapping("/profile/{username}")
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

    private ResponseUserDTO convertToDto(Account account) {
        Set<RoleEntity> roles = account.getUser().getRoles();
        String userRole = roles.stream()
                .map(RoleEntity::getName)
                .findFirst()
                .map(ERole::toString)
                .orElse(null);
        return new ResponseUserDTO(
                account.getUser().getId(),
                account.getUser().getUsername(),
                account.getUser().getFirstName(),
                account.getUser().getLastName(),
                account.getUser().getEmail(),
                account.getId(),
                userRole
        );
    }

    public UserProfileDTO convertProfileDTO(Subscription subscription) {
        UserEntity user = subscription.getAccount().getUser();
        Optional<Account> accountOptional = accountRepository.findById(subscription.getAccount().getId());

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setFirstName(user.getFirstName());
        userProfileDTO.setLastName(user.getLastName());
        userProfileDTO.setEmail(user.getEmail());

        String userRole = user.getRoles().stream()
                .findFirst()
                .map(RoleEntity::getName)
                .map(ERole::toString)
                .orElse(null);
        userProfileDTO.setRol(userRole);

        accountOptional.ifPresent(account -> {
            userProfileDTO.setDocument(account.getDocument());
            userProfileDTO.setAccountId(account.getId());
            userProfileDTO.setCreditBalance(account.getCreditBalance());
            userProfileDTO.setRank(account.getRank().getName().toString());
            userProfileDTO.setSubscriptionId(subscription.getId());
            userProfileDTO.setSubscription(subscription.getName());
            userProfileDTO.setPlanType(subscription.getPlanType());
            userProfileDTO.setIsExpired(subscription.getEndDate().isBefore(LocalDate.now()));
        });

        return userProfileDTO;
    }
}