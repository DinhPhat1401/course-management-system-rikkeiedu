package edu.rk.camel.coursemanagementsystem.model.dto.response;

import edu.rk.camel.coursemanagementsystem.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
