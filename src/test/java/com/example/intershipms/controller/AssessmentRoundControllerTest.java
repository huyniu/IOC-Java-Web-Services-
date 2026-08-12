package com.example.intershipms.controller;

import com.example.intershipms.dto.request.AssessmentRoundRequest;
import com.example.intershipms.dto.request.AssignCriteriaRequest;
import com.example.intershipms.entity.AssessmentRound;
import com.example.intershipms.entity.EvaluationCriteria;
import com.example.intershipms.entity.InternshipPhase;
import com.example.intershipms.repository.AssessmentRoundRepository;
import com.example.intershipms.repository.EvaluationCriteriaRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AssessmentRoundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssessmentRoundRepository roundRepository;

    @Autowired
    private InternshipPhaseRepository phaseRepository;

    @Autowired
    private EvaluationCriteriaRepository criteriaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private InternshipPhase phase;
    private EvaluationCriteria criteria;

    @BeforeEach
    void setUp() {
        roundRepository.deleteAll();
        criteriaRepository.deleteAll();
        phaseRepository.deleteAll();

        phase = InternshipPhase.builder()
                .phaseName("Phase Test 1")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phase = phaseRepository.save(phase);

        criteria = EvaluationCriteria.builder()
                .criterionName("Bao cao giua ky")
                .maxScore(new BigDecimal("10.00"))
                .build();
        criteria = criteriaRepository.save(criteria);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("1. Happy Path: Tạo đợt đánh giá thành công")
    void createRound_Success() throws Exception {
        AssessmentRoundRequest request = AssessmentRoundRequest.builder()
                .phaseId(phase.getPhaseId())
                .roundName("Danh gia Midterm")
                .startDate(LocalDate.of(2025, 10, 1))
                .endDate(LocalDate.of(2025, 10, 15))
                .description("Dot 1")
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/rounds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roundName").value("Danh gia Midterm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("2. Happy Path: Gán tiêu chí kèm trọng số vào đợt đánh giá")
    void assignCriteria_Success() throws Exception {
        AssessmentRound round = AssessmentRound.builder()
                .phase(phase)
                .roundName("Danh gia Final")
                .startDate(LocalDate.of(2025, 11, 1))
                .endDate(LocalDate.of(2025, 11, 15))
                .build();
        round = roundRepository.save(round);

        AssignCriteriaRequest request = AssignCriteriaRequest.builder()
                .criterionId(criteria.getCriterionId())
                .weight(new BigDecimal("0.40"))
                .build();

        mockMvc.perform(post("/api/rounds/" + round.getRoundId() + "/criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.weight").value(0.40));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("3. Invalid Input: phaseId không tồn tại")
    void createRound_InvalidPhaseId() throws Exception {
        AssessmentRoundRequest request = AssessmentRoundRequest.builder()
                .phaseId(9999)
                .roundName("Danh gia Midterm")
                .startDate(LocalDate.of(2025, 10, 1))
                .endDate(LocalDate.of(2025, 10, 15))
                .build();

        mockMvc.perform(post("/api/rounds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
