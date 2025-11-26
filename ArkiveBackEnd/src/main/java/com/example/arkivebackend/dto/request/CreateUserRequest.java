package com.example.arkivebackend.dto.request;

import com.example.arkivebackend.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;

@Data // Get, set,
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    
    String fullName;

    @Size(min = 3, max = 50, message = "USERNAME_SIZE_INVALID")
    String username;
    
    @Email(message = "EMAIL_INVALID")
    String email;
    
    @Size(min = 6, max = 20, message = "PASSWORD_SIZE_INVALID")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$", message = "PASSWORD_PATTERN_INVALID")
    String password;

    Set<Role> roles;
}
