package com.gym.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.management.relation.Role;
import java.util.HashSet;
import java.util.Set;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private String password;
        private Set<Role> roles = new HashSet<>();
}
