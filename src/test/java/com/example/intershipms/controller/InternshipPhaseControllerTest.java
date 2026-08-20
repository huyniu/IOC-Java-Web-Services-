package com.example.intershipms.controller;

import com.example.intershipms.dto.request.InternshipPhaseRequest;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    private InternshipAssignmentRepository assignmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        assignmentRepository.deleteAll();
        studentRepository.deleteAll();
        mentorRepository.deleteAll();
        phaseRepository.deleteAll();
        userRepository.deleteAll();
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

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("5. Delete Phase Success: Xóa giai đoạn thực tập chưa có sinh viên thành công")
    void deletePhase_Success() throws Exception {
        InternshipPhase phase = InternshipPhase.builder()
                .phaseName("Phase Empty Delete")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phase = phaseRepository.save(phase);

        mockMvc.perform(delete("/api/phases/" + phase.getPhaseId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("6. Delete Phase Error: Thất bại khi xóa giai đoạn thực tập đang có sinh viên theo học")
    void deletePhase_HasStudents_ReturnsBadRequest() throws Exception {
        InternshipPhase phase = InternshipPhase.builder()
                .phaseName("Phase Active With Student")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phase = phaseRepository.save(phase);

        User uStudent = User.builder()
                .username("sv_test_del")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Sinh Vien Del Test")
                .email("sv_del@test.com")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
        uStudent = userRepository.save(uStudent);

        Student student = Student.builder()
                .user(uStudent)
                .studentCode("SV_DEL_01")
                .build();
        student = studentRepository.save(student);

        User uMentor = User.builder()
                .username("mt_test_del")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Mentor Del Test")
                .email("mt_del@test.com")
                .role(User.Role.MENTOR)
                .isActive(true)
                .build();
        uMentor = userRepository.save(uMentor);

        Mentor mentor = Mentor.builder()
                .user(uMentor)
                .department("Khoa CNTT")
                .build();
        mentor = mentorRepository.save(mentor);

        InternshipAssignment assignment = InternshipAssignment.builder()
                .phase(phase)
                .student(student)
                .mentor(mentor)
                .build();
        assignmentRepository.save(assignment);

        mockMvc.perform(delete("/api/phases/" + phase.getPhaseId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Không thể xóa giai đoạn thực tập đang có sinh viên theo học!"));
    }
}
