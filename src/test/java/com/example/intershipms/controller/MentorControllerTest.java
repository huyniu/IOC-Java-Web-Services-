package com.example.intershipms.controller;

import com.example.intershipms.dto.request.MentorRequest;
import com.example.intershipms.entity.User;
import com.example.intershipms.repository.MentorRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MentorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User mentorUser;

    @BeforeEach
    void setUp() {
        mentorRepository.deleteAll();
        userRepository.deleteAll();

        mentorUser = User.builder()
                .username("mentor01")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Le Van Mentor")
                .email("mentor01@test.com")
                .role(User.Role.MENTOR)
                .isActive(true)
                .build();
        mentorUser = userRepository.save(mentorUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("1. Happy Path: Admin tạo hồ sơ giảng viên hướng dẫn thành công")
    void createMentor_Success() throws Exception {
        MentorRequest request = MentorRequest.builder()
                .userId(mentorUser.getUserId())
                .department("Khoa CNTT")
                .academicRank("Thac si")
                .build();

        mockMvc.perform(post("/api/mentors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Le Van Mentor"))
                .andExpect(jsonPath("$.data.department").value("Khoa CNTT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("2. Invalid Input: userId để trống")
    void createMentor_InvalidInput() throws Exception {
        MentorRequest request = MentorRequest.builder()
                .userId(null)
                .department("Khoa CNTT")
                .build();

        mockMvc.perform(post("/api/mentors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("3. Unauthorized/Forbidden: Role STUDENT không thể tạo Mentor profile")
    void createMentor_Forbidden() throws Exception {
        MentorRequest request = MentorRequest.builder()
                .userId(mentorUser.getUserId())
                .department("Khoa CNTT")
                .build();

        mockMvc.perform(post("/api/mentors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
