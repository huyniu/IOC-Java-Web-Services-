package com.example.intershipms.dto.response;

import com.example.intershipms.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Integer userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private User.Role role;
    private Boolean isActive;
}