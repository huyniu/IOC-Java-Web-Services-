package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.LoginRequest;
import com.example.intershipms.dto.response.JwtResponse;
import com.example.intershipms.security.JwtTokenProvider;
import com.example.intershipms.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. Xác thực tài khoản (Spring Security sẽ tự động gọi UserDetailsServiceImpl)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Nếu xác thực thành công, lưu thông tin vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Tạo Token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Lấy thông tin user hiện tại
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_STUDENT"); // Default fallback

        // 5. Đóng gói dữ liệu trả về
        JwtResponse jwtResponse = JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getUserId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .role(role)
                .build();

        // 6. Bọc trong ApiResponse chuẩn form
        ApiResponse<JwtResponse> response = ApiResponse.<JwtResponse>builder()
                .success(true)
                .message("Đăng nhập thành công")
                .data(jwtResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}