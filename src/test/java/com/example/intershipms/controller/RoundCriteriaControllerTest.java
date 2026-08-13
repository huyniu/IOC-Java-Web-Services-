package com.example.intershipms.controller;

import com.example.intershipms.dto.request.RoundCriteriaRequest;
import com.example.intershipms.entity.AssessmentRound;
import com.example.intershipms.entity.EvaluationCriteria;
import com.example.intershipms.entity.InternshipPhase;
import com.example.intershipms.repository.AssessmentRoundRepository;
import com.example.intershipms.repository.EvaluationCriteriaRepository;
import com.example.intershipms.repository.InternshipPhaseRepository;
import com.example.intershipms.repository.RoundCriteriaRepository;
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
public class RoundCriteriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoundCriteriaRepository roundCriteriaRepository;

    @Autowired
    private AssessmentRoundRepository roundRepository;

    @Autowired
    private InternshipPhaseRepository phaseRepository;

    @Autowired
    private EvaluationCriteriaRepository criteriaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AssessmentRound round;
    private EvaluationCriteria criteria;

    @BeforeEach
    void setUp() {
        roundCriteriaRepository.deleteAll();
        roundRepository.deleteAll();
        criteriaRepository.deleteAll();
        phaseRepository.deleteAll();

        InternshipPhase phase = InternshipPhase.builder()
                .phaseName("Phase Test")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phase = phaseRepository.save(phase);

        round = AssessmentRound.builder()
                .phase(phase)
                .roundName("Round Test")
                .startDate(LocalDate.of(2025, 10, 1))
                .endDate(LocalDate.of(2025, 10, 15))
                .build();
        round = roundRepository.save(round);

        criteria = EvaluationCriteria.builder()
                .criterionName("Bao cao giua ky")
                .maxScore(new BigDecimal("10.00"))
                .build();
        criteria = criteriaRepository.save(criteria);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Tạo RoundCriteria qua POST /api/round_criteria thành công với JSON Body")
    void createRoundCriteria_WithRequestBody_Success() throws Exception {
        RoundCriteriaRequest request = RoundCriteriaRequest.builder()
                .roundId(round.getRoundId())
                .criterionId(criteria.getCriterionId())
                .weight(new BigDecimal("0.50"))
                .build();

        mockMvc.perform(post("/api/round_criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roundId").value(round.getRoundId()))
                .andExpect(jsonPath("$.data.criterionId").value(criteria.getCriterionId()))
                .andExpect(jsonPath("$.data.weight").value(0.50));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Lấy danh sách RoundCriteria theo round_id thành công")
    void getRoundCriteria_WithRoundId_Success() throws Exception {
        RoundCriteriaRequest request = RoundCriteriaRequest.builder()
                .roundId(round.getRoundId())
                .criterionId(criteria.getCriterionId())
                .weight(new BigDecimal("0.50"))
                .build();

        mockMvc.perform(post("/api/round_criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/round_criteria").param("round_id", String.valueOf(round.getRoundId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].roundId").value(round.getRoundId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Lấy tất cả RoundCriteria khi không truyền round_id")
    void getRoundCriteria_WithoutRoundId_Success() throws Exception {
        RoundCriteriaRequest request = RoundCriteriaRequest.builder()
                .roundId(round.getRoundId())
                .criterionId(criteria.getCriterionId())
                .weight(new BigDecimal("0.50"))
                .build();

        mockMvc.perform(post("/api/round_criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/round_criteria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}
