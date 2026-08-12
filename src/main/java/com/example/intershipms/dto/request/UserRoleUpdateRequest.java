package com.example.intershipms.dto.request;

import com.example.intershipms.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleUpdateRequest {

    @NotNull(message = "Quyền (Role) không được để trống")
    private User.Role role;
}
