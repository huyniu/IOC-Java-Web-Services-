package com.example.intershipms.controller;

import com.example.intershipms.dto.request.EvaluationCriteriaRequest;
import com.example.intershipms.entity.EvaluationCriteria;
import com.example.intershipms.repository.EvaluationCriteriaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EvaluationCriteriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EvaluationCriteriaRepository criteriaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        criteriaRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("1. Happy Path: Admin tạo tiêu chí đánh giá mới")
    void createCriteria_Success() throws Exception {
        EvaluationCriteriaRequest request = EvaluationCriteriaRequest.builder()
                .criterionName("Kyna kynang gieo tiep")
                .description("Danh gia ky nang giao tiep")
                .maxScore(new BigDecimal("10.00"))
                .build();

        mockMvc.perform(post("/api/criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.criterionName").value("Kyna kynang gieo tiep"))
                .andExpect(jsonPath("$.data.maxScore").value(10.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("2. Invalid Input: maxScore = 0")
    void createCriteria_InvalidMaxScore() throws Exception {
        EvaluationCriteriaRequest request = EvaluationCriteriaRequest.builder()
                .criterionName("Kynang kythuat")
                .maxScore(BigDecimal.ZERO)
                .build();

        mockMvc.perform(post("/api/criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("3. Unauthorized/Forbidden: STUDENT không có quyền tạo tiêu chí")
    void createCriteria_ForbiddenForStudent() throws Exception {
        EvaluationCriteriaRequest request = EvaluationCriteriaRequest.builder()
                .criterionName("Kynang kythuat")
                .maxScore(new BigDecimal("10.00"))
                .build();

        mockMvc.perform(post("/api/criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("4. Conflict: Trùng tên tiêu chí đánh giá")
    void createCriteria_ConflictName() throws Exception {
        EvaluationCriteria existing = EvaluationCriteria.builder()
                .criterionName("Bao cao thuc tap")
                .maxScore(new BigDecimal("10.00"))
                .build();
        criteriaRepository.save(existing);

        EvaluationCriteriaRequest request = EvaluationCriteriaRequest.builder()
                .criterionName("Bao cao thuc tap")
                .maxScore(new BigDecimal("10.00"))
                .build();

        mockMvc.perform(post("/api/criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
