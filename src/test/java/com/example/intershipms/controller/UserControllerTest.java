package com.example.intershipms.controller;

import com.example.intershipms.dto.request.UserCreationRequest;
import com.example.intershipms.dto.request.UserRoleUpdateRequest;
import com.example.intershipms.entity.User;
import com.example.intershipms.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("1. Happy Path: Admin tạo tài khoản mới thành công")
    void createUser_Success() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .username("student01")
                .password("password123")
                .fullName("Nguyen Van A")
                .email("student01@test.com")
                .phoneNumber("0987654321")
                .role(User.Role.STUDENT)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("student01"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("2. Invalid Input: Email sai định dạng, username để trống")
    void createUser_InvalidInput() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .username("")
                .password("123")
                .fullName("")
                .email("invalid-email")
                .role(User.Role.STUDENT)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("3. Unauthorized/Forbidden: Role STUDENT không có quyền tạo user")
    void createUser_ForbiddenForStudent() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .username("student02")
                .password("password123")
                .fullName("Nguyen Van B")
                .email("student02@test.com")
                .role(User.Role.STUDENT)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("4. Conflict/Business: Trùng username")
    void createUser_ConflictUsername() throws Exception {
        User existing = User.builder()
                .username("student01")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Nguyen Van A")
                .email("existing@test.com")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
        userRepository.save(existing);

        UserCreationRequest request = UserCreationRequest.builder()
                .username("student01")
                .password("password123")
                .fullName("Nguyen Van A Dual")
                .email("newemail@test.com")
                .role(User.Role.STUDENT)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("5. Business Validation: Chặn giáng cấp tài khoản Admin")
    void updateUserRole_DemoteAdmin_Fails() throws Exception {
        User adminUser = User.builder()
                .username("admin_test")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Admin Test")
                .email("admin_test@test.com")
                .role(User.Role.ADMIN)
                .isActive(true)
                .build();
        adminUser = userRepository.save(adminUser);

        UserRoleUpdateRequest request = UserRoleUpdateRequest.builder()
                .role(User.Role.STUDENT)
                .build();

        mockMvc.perform(put("/api/users/" + adminUser.getUserId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Không thể giáng cấp tài khoản Admin!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("6. Happy Path: Cập nhật role cho user thành công")
    void updateUserRole_Success() throws Exception {
        User studentUser = User.builder()
                .username("student_test")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Student Test")
                .email("student_test@test.com")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
        studentUser = userRepository.save(studentUser);

        UserRoleUpdateRequest request = UserRoleUpdateRequest.builder()
                .role(User.Role.MENTOR)
                .build();

        mockMvc.perform(put("/api/users/" + studentUser.getUserId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("MENTOR"));
    }
}
