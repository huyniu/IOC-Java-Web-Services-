package com.example.intershipms.controller;

import com.example.intershipms.dto.request.InternshipPhaseRequest;
import com.example.intershipms.entity.InternshipPhase;
import com.example.intershipms.repository.InternshipPhaseRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InternshipPhaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InternshipPhaseRepository phaseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        phaseRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("1. Happy Path: Tạo giai đoạn thực tập thành công")
    void createPhase_Success() throws Exception {
        InternshipPhaseRequest request = InternshipPhaseRequest.builder()
                .phaseName("Thuc tap HK1 2025-2026")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .description("Giai doan thuc tap HK1")
                .build();

        mockMvc.perform(post("/api/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.phaseName").value("Thuc tap HK1 2025-2026"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("2. Invalid Input: Ngày bắt đầu sau ngày kết thúc")
    void createPhase_InvalidDates() throws Exception {
        InternshipPhaseRequest request = InternshipPhaseRequest.builder()
                .phaseName("Thuc tap sai ngay")
                .startDate(LocalDate.of(2025, 12, 31))
                .endDate(LocalDate.of(2025, 9, 1))
                .build();

        mockMvc.perform(post("/api/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("3. Unauthorized/Forbidden: STUDENT không được tạo giai đoạn thực tập")
    void createPhase_ForbiddenForStudent() throws Exception {
        InternshipPhaseRequest request = InternshipPhaseRequest.builder()
                .phaseName("Thuc tap HK1")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();

        mockMvc.perform(post("/api/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("4. Conflict: Trùng tên giai đoạn thực tập")
    void createPhase_ConflictName() throws Exception {
        InternshipPhase existing = InternshipPhase.builder()
                .phaseName("Thuc tap HK1 2025-2026")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phaseRepository.save(existing);

        InternshipPhaseRequest request = InternshipPhaseRequest.builder()
                .phaseName("Thuc tap HK1 2025-2026")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();

        mockMvc.perform(post("/api/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
