package com.example.intershipms.controller;

import com.example.intershipms.dto.request.InternshipAssignmentRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InternshipAssignmentControllerTest {

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
    private InternshipAssignmentRepository assignmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Student student;
    private Mentor mentor;
    private InternshipPhase phase;

    @BeforeEach
    void setUp() {
        assignmentRepository.deleteAll();
        studentRepository.deleteAll();
        mentorRepository.deleteAll();
        phaseRepository.deleteAll();
        userRepository.deleteAll();

        User uStudent = User.builder()
                .username("sv_assign")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Sinh Vien Assign")
                .email("sv_assign@test.com")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
        uStudent = userRepository.save(uStudent);

        student = Student.builder()
                .user(uStudent)
                .studentCode("SV_ASSIGN_01")
                .build();
        student = studentRepository.save(student);

        User uMentor = User.builder()
                .username("mt_assign")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Mentor Assign")
                .email("mt_assign@test.com")
                .role(User.Role.MENTOR)
                .isActive(true)
                .build();
        uMentor = userRepository.save(uMentor);

        mentor = Mentor.builder()
                .user(uMentor)
                .department("Khoa CNTT")
                .build();
        mentor = mentorRepository.save(mentor);

        phase = InternshipPhase.builder()
                .phaseName("Phase Assign 1")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phase = phaseRepository.save(phase);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("1. Happy Path: Admin phân công thực tập thành công")
    void assignStudent_Success() throws Exception {
        InternshipAssignmentRequest request = InternshipAssignmentRequest.builder()
                .studentId(student.getStudentId())
                .mentorId(mentor.getMentorId())
                .phaseId(phase.getPhaseId())
                .build();

        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentName").value("Sinh Vien Assign"))
                .andExpect(jsonPath("$.data.mentorName").value("Mentor Assign"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("2. Conflict/Business: Sinh viên đã được phân công trong giai đoạn này")
    void assignStudent_ConflictDuplicate() throws Exception {
        InternshipAssignment existing = InternshipAssignment.builder()
                .student(student)
                .mentor(mentor)
                .phase(phase)
                .build();
        assignmentRepository.save(existing);

        InternshipAssignmentRequest request = InternshipAssignmentRequest.builder()
                .studentId(student.getStudentId())
                .mentorId(mentor.getMentorId())
                .phaseId(phase.getPhaseId())
                .build();

        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("3. Unauthorized/Forbidden: STUDENT không thể thực hiện phân công")
    void assignStudent_Forbidden() throws Exception {
        InternshipAssignmentRequest request = InternshipAssignmentRequest.builder()
                .studentId(student.getStudentId())
                .mentorId(mentor.getMentorId())
                .phaseId(phase.getPhaseId())
                .build();

        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
