package com.example.intershipms.controller;

import com.example.intershipms.dto.request.AssessmentResultRequest;
import com.example.intershipms.entity.*;
import com.example.intershipms.repository.*;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AssessmentResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private InternshipPhaseRepository phaseRepository;

    @Autowired
    private AssessmentRoundRepository roundRepository;

    @Autowired
    private EvaluationCriteriaRepository criteriaRepository;

    @Autowired
    private InternshipAssignmentRepository assignmentRepository;

    @Autowired
    private AssessmentResultRepository resultRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User mentorUser;
    private Student student;
    private Mentor mentor;
    private InternshipPhase phase;
    private AssessmentRound round;
    private EvaluationCriteria criteria;
    private InternshipAssignment assignment;

    @BeforeEach
    void setUp() {
        resultRepository.deleteAll();
        assignmentRepository.deleteAll();
        roundRepository.deleteAll();
        criteriaRepository.deleteAll();
        studentRepository.deleteAll();
        mentorRepository.deleteAll();
        phaseRepository.deleteAll();
        userRepository.deleteAll();

        mentorUser = User.builder()
                .username("mentor_eval")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Mentor Evaluator")
                .email("mentor_eval@test.com")
                .role(User.Role.MENTOR)
                .isActive(true)
                .build();
        mentorUser = userRepository.save(mentorUser);

        mentor = Mentor.builder()
                .user(mentorUser)
                .department("Khoa CNTT")
                .build();
        mentor = mentorRepository.save(mentor);

        User uStudent = User.builder()
                .username("sv_eval")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Sinh Vien Evaluated")
                .email("sv_eval@test.com")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
        uStudent = userRepository.save(uStudent);

        student = Student.builder()
                .user(uStudent)
                .studentCode("SV_EVAL_01")
                .build();
        student = studentRepository.save(student);

        phase = InternshipPhase.builder()
                .phaseName("Phase Eval 1")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phase = phaseRepository.save(phase);

        round = AssessmentRound.builder()
                .phase(phase)
                .roundName("Dot 1 Eval")
                .startDate(LocalDate.of(2025, 10, 1))
                .endDate(LocalDate.of(2025, 10, 15))
                .build();
        round = roundRepository.save(round);

        criteria = EvaluationCriteria.builder()
                .criterionName("Bao cao thuc tap")
                .maxScore(new BigDecimal("10.00"))
                .build();
        criteria = criteriaRepository.save(criteria);

        assignment = InternshipAssignment.builder()
                .student(student)
                .mentor(mentor)
                .phase(phase)
                .build();
        assignment = assignmentRepository.save(assignment);
    }

    @Test
    @WithMockUser(username = "mentor_eval", roles = "MENTOR")
    @DisplayName("1. Happy Path: Mentor gửi kết quả đánh giá cho Sinh viên")
    void submitAssessment_Success() throws Exception {
        AssessmentResultRequest request = AssessmentResultRequest.builder()
                .assignmentId(assignment.getAssignmentId())
                .roundId(round.getRoundId())
                .criterionId(criteria.getCriterionId())
                .score(new BigDecimal("8.50"))
                .comments("Hoan thanh tot")
                .build();

        mockMvc.perform(post("/api/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.score").value(8.50))
                .andExpect(jsonPath("$.data.comments").value("Hoan thanh tot"));
    }

    @Test
    @WithMockUser(username = "mentor_eval", roles = "MENTOR")
    @DisplayName("2. Invalid Input: Điểm số nhập vượt quá điểm tối đa")
    void submitAssessment_ScoreExceedsMax() throws Exception {
        AssessmentResultRequest request = AssessmentResultRequest.builder()
                .assignmentId(assignment.getAssignmentId())
                .roundId(round.getRoundId())
                .criterionId(criteria.getCriterionId())
                .score(new BigDecimal("15.00"))
                .comments("Diem vuot max")
                .build();

        mockMvc.perform(post("/api/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("3. Unauthorized/Forbidden: STUDENT không có quyền đánh giá điểm")
    void submitAssessment_ForbiddenForStudent() throws Exception {
        AssessmentResultRequest request = AssessmentResultRequest.builder()
                .assignmentId(assignment.getAssignmentId())
                .roundId(round.getRoundId())
                .criterionId(criteria.getCriterionId())
                .score(new BigDecimal("9.00"))
                .build();

        mockMvc.perform(post("/api/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("4. Happy Path: Sinh viên xem kết quả đánh giá theo Giai đoạn")
    void getStudentSummaryByPhase_Success() throws Exception {
        AssessmentResult result = AssessmentResult.builder()
                .assignment(assignment)
                .round(round)
                .criterion(criteria)
                .score(new BigDecimal("9.00"))
                .comments("Diem A")
                .evaluatedBy(mentorUser)
                .build();
        resultRepository.save(result);

        mockMvc.perform(get("/api/assessments/student/" + student.getStudentId() + "/phase/" + phase.getPhaseId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentId").value(student.getStudentId()))
                .andExpect(jsonPath("$.data.totalWeightedScore").value(9.00));
    }
}
