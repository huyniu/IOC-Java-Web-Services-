package com.example.intershipms.controller;

import com.example.intershipms.dto.request.StudentRequest;
import com.example.intershipms.entity.Student;
import com.example.intershipms.entity.User;
import com.example.intershipms.repository.StudentRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User studentUser;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        userRepository.deleteAll();

        studentUser = User.builder()
                .username("sv001")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Tran Van C")
                .email("sv001@test.com")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
        studentUser = userRepository.save(studentUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("1. Happy Path: Tạo hồ sơ sinh viên thành công")
    void createStudent_Success() throws Exception {
        StudentRequest request = StudentRequest.builder()
                .userId(studentUser.getUserId())
                .studentCode("SV001")
                .major("CNTT")
                .className("D18CNTT1")
                .dateOfBirth(LocalDate.of(2002, 5, 10))
                .address("Ha Noi")
                .build();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentCode").value("SV001"))
                .andExpect(jsonPath("$.data.fullName").value("Tran Van C"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("2. Invalid Input: Mã sinh viên để trống")
    void createStudent_InvalidInput() throws Exception {
        StudentRequest request = StudentRequest.builder()
                .userId(studentUser.getUserId())
                .studentCode("")
                .major("CNTT")
                .build();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("3. Unauthorized/Forbidden: Role STUDENT không có quyền tạo hồ sơ sinh viên mới")
    void createStudent_Forbidden() throws Exception {
        StudentRequest request = StudentRequest.builder()
                .userId(studentUser.getUserId())
                .studentCode("SV002")
                .major("CNTT")
                .className("D18CNTT1")
                .build();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("4. Conflict: Trùng mã sinh viên")
    void createStudent_ConflictCode() throws Exception {
        Student existing = Student.builder()
                .user(studentUser)
                .studentCode("SV001")
                .major("CNTT")
                .build();
        studentRepository.save(existing);

        User anotherUser = User.builder()
                .username("sv002")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Tran Van D")
                .email("sv002@test.com")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
        anotherUser = userRepository.save(anotherUser);

        StudentRequest request = StudentRequest.builder()
                .userId(anotherUser.getUserId())
                .studentCode("SV001")
                .major("KTPM")
                .build();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
