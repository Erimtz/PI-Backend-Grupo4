package com.gym.security.services;//package com.gym.security.services;

import com.gym.dto.response.Message;
import com.gym.entities.*;
import com.gym.enums.ERank;
import com.gym.exceptions.*;
import com.gym.mail.services.EmailService;
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
import com.gym.enums.ERole;
import com.gym.security.repositories.RoleRepository;
import com.gym.security.repositories.UserRepository;
import com.gym.services.impl.AccountService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, AccountRepository accountRepository,
                       AccountService accountService, RankRepository rankRepository, SubscriptionRepository subscriptionRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.rankRepository = rankRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    public UserProfileDTO getUserProfile(String username, String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("No valid authorization token found.");
        }
        token = token.substring(7);

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (username == null) {
            throw new UnauthorizedException("Unable to retrieve the username from the token.");
        } else if (!username.equals(tokenUsername)) {
            throw new UnauthorizedException("The user to access does not match the authenticated user.");
        }

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found.");
        }
        UserEntity user = optionalUser.get();
        Account account = accountRepository.findByUserId(optionalUser.get().getId()).orElse(null);
        Subscription subscription = subscriptionRepository.findByAccountId(account.getId()).orElse(null);
        UserProfileDTO userProfileDTO = convertProfileDTO(subscription);
        return userProfileDTO;
    }

    public ResponseUserDTO getUserById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID:  " + userId));
        return convertToUserDto(user);
    }

    public List<ResponseUserDTO> getAllUsers() {
        Iterable<UserEntity> usersIterable = userRepository.findAll();
        List<ResponseUserDTO> responseUserDTOList = new ArrayList<>();
        for (UserEntity user : usersIterable) {
            ResponseUserDTO responseUserDTO = convertToUserDto(user);
            responseUserDTOList.add(responseUserDTO);
        }
        if (responseUserDTOList.isEmpty()) {
            throw new EmptyUserListException("The list of users is empty.");
        }
        return responseUserDTOList;
    }

    public UserEntity createAdminUser(CreateUserDTO createUserDTO) {

        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new EmailAlreadyExistsException("The email is already in use.");
        }

        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("The username is already in use.");
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
            throw new EmailAlreadyExistsException("The email is already in use.");
        }

        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("The username is already in use.");
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
        String document = createUserDTO.getDocument();
        Account account = Account.builder()
                .user(userEntity)
                .transferList(new ArrayList<>())
                .couponList(new ArrayList<>())
                .purchaseList(new ArrayList<>())
                .creditBalance(BigDecimal.valueOf(0.0))
                .rank(rankAccount)
                .document(createUserDTO.getDocument())
                .build();
        Account savedAccount = accountService.createAccount(userEntity, document);

        ResponseUserDTO responseUserDTO = convertToDto(savedAccount);

//        emailService.sendEmailNewUser(responseUserDTO);

        return responseUserDTO;
    }

    public ResponseUserDTO updateUser(String username, UpdateUserDTO updateUserDTO, String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("No valid authorization token found.");
        }
        token = token.substring(7);

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (username == null) {
            throw new UnauthorizedException("Unable to retrieve the username from the token.");
        } else if (!username.equals(tokenUsername)) {
            throw new UnauthorizedException("The user to modify does not match the authenticated user.");
        }

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found.");
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
            throw new UnauthorizedException("No valid authorization token found.");
        }
        token = token.substring(7);

        String username = jwtUtils.getUsernameFromToken(token);

        if (username == null) {
            throw new UnauthorizedException("Unable to retrieve the username from the token.");
        }

        String currentPassword = changePasswordDTO.getCurrentPassword();
        String newPassword = changePasswordDTO.getNewPassword();
        String confirmPassword = changePasswordDTO.getConfirmPassword();

        if (currentPassword.equals(newPassword)) {
            throw new BadRequestException("The new password must be different from the current password.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("The new password and password confirmation do not match.");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("The current password is incorrect.");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("The user with ID: " + id + " does not exists.");
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
            throw new CustomException(HttpStatus.BAD_REQUEST, "That username already exists.");
        if(userRepository.existsByEmail(createUserDTO.getEmail()))
            throw new CustomException(HttpStatus.BAD_REQUEST, "That user email already exists.");
        UserEntity user =
                new UserEntity(createUserDTO.getFirstName(), createUserDTO.getLastName(), createUserDTO.getUsername(), createUserDTO.getEmail(),
                        passwordEncoder.encode(createUserDTO.getPassword()));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.USER).get());
        user.setRoles(roles);
        userRepository.save(user);
        return new Message(user.getUsername() + " has been created");
    }

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

    private ResponseUserDTO convertToUserDto(UserEntity user) {
        return new ResponseUserDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                determineRole(user.getRoles())
        );
    }

    private String determineRole(Set<RoleEntity> roles) {
        return roles.stream()
                .map(RoleEntity::getName)
                .findFirst()
                .map(ERole::toString)
                .orElse(null);
    }
}