package com.example.intershipms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Integer id;
    private String username;
    private String email;
    private String role;
}