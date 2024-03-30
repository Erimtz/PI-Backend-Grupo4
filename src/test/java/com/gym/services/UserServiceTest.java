//package com.gym.services.user;
//
//import com.gym.ProyectoIntegradorGymApplication;
//import com.gym.enums.ERole;
//import com.gym.security.controllers.request.CreateUserDTO;
//import com.gym.security.controllers.request.UpdateUserDTO;
//import com.gym.security.controllers.response.ResponseUserDTO;
//import com.gym.security.entities.RoleEntity;
//import com.gym.security.entities.UserEntity;
//import com.gym.security.repositories.RoleRepository;
//import com.gym.security.repositories.UserRepository;
//import com.gym.security.services.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(classes = ProyectoIntegradorGymApplication.class)
//public class UserServiceTest {
//
//    @Autowired
//    private UserService userService;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    @MockBean
//    private RoleRepository roleRepository;
//
//    @Test
//    public void testCreateUser() {
//        // Arrange
//        CreateUserDTO createUserDTO = new CreateUserDTO();
//        createUserDTO.setUsername("testUser");
//        createUserDTO.setEmail("test@example.com");
//        createUserDTO.setPassword("password");
//        createUserDTO.setFirstName("Test");
//        createUserDTO.setLastName("User");
//        createUserDTO.setRoles(new HashSet<>());
//
//        // Mock UserRepository behavior
//        when(userRepository.existsByUsername(anyString())).thenReturn(false);
//        when(userRepository.existsByEmail(anyString())).thenReturn(false);
//
//        // Mock RoleRepository behavior
//        RoleEntity userRole = new RoleEntity();
//        userRole.setName(ERole.USER);
//        when(roleRepository.findByName(ERole.USER)).thenReturn(Optional.of(userRole));
//
//        // Act
//        userService.createUser(createUserDTO);
//
//        // Assert
//        verify(userRepository, times(1)).save(any(UserEntity.class));
//    }
//
//    @Test
//    public void testGetUserById() {
//        // Arrange
//        Long userId = 1L;
//        UserEntity user = new UserEntity();
//        user.setId(userId);
//        user.setUsername("testUser");
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        // Act
//        ResponseUserDTO result = userService.getUserById(userId);
//
//        // Assert
//        assertEquals(user.getId(), result.getId());
//        assertEquals(user.getUsername(), result.getUsername());
//    }
//
//    @Test
//    public void testUpdateUser() {
//        // Arrange
//        String username = "testUser";
//        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
//        updateUserDTO.setEmail("updated@example.com");
//        updateUserDTO.setFirstName("Updated");
//        updateUserDTO.setLastName("User");
//
//        UserEntity existingUser = new UserEntity();
//        existingUser.setUsername(username);
//        existingUser.setEmail("old@example.com");
//        existingUser.setFirstName("Old");
//        existingUser.setLastName("User");
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
//
//        // Act
//        ResponseUserDTO updatedUser = userService.updateUser(username, updateUserDTO, "dummyToken");
//
//        // Assert
//        assertEquals(updateUserDTO.getEmail(), updatedUser.getEmail());
//        assertEquals(updateUserDTO.getFirstName(), updatedUser.getFirstName());
//        assertEquals(updateUserDTO.getLastName(), updatedUser.getLastName());
//    }
//}