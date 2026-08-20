package com.example.intershipms.controller;

// Các DTO request chứa dữ liệu đầu vào gửi lên từ Client
import com.example.intershipms.dto.request.InternshipPhaseRequest;
// Các Entity đại diện cho các bảng dữ liệu trong Database
import com.example.intershipms.entity.*;
// Các Repository interface giúp thao tác truy vấn Database thông qua Spring Data JPA
import com.example.intershipms.repository.*;
// ObjectMapper từ thư viện Jackson (com.fasterxml.jackson.databind.ObjectMapper): dùng để chuyển đối tượng Java thành JSON String và ngược lại
import com.fasterxml.jackson.databind.ObjectMapper;
// JUnit 5 (org.junit.jupiter.api): Thư viện testing chuẩn trong Spring Boot
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// Spring Core (org.springframework.beans.factory.annotation.Autowired): Dùng để tự động tiêm (inject) các Bean từ Spring Context
import org.springframework.beans.factory.annotation.Autowired;
// Spring Boot Test: Tự động cấu hình MockMvc để giả lập gửi HTTP Request tới Controller mà không cần bật server thật
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
// Spring Security: Mã hóa mật khẩu user
import org.springframework.security.crypto.password.PasswordEncoder;
// Spring Security Test: Giả lập thông tin User đã đăng nhập với Role cụ thể trong môi trường Test
import org.springframework.security.test.context.support.WithMockUser;
// MockMvc (org.springframework.test.web.servlet.MockMvc): Đối tượng chính để thực thi các request HTTP giả lập
import org.springframework.test.web.servlet.MockMvc;
// Transactional (org.springframework.transaction.annotation.Transactional): Tự động rollback dữ liệu sau mỗi test case
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

// Các hàm static từ MockMvcRequestBuilders: dùng để dựng request HTTP GET, POST, DELETE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// Các hàm static từ MockMvcResultMatchers: dùng để kiểm tra (assert) HTTP status và nội dung JSON trả về
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest: Khởi tạo toàn bộ ứng dụng Spring Boot trong môi trường test
@SpringBootTest
// @AutoConfigureMockMvc: Tự động khởi tạo và cấu hình đối tượng MockMvc cho test
@AutoConfigureMockMvc
// @Transactional: Mỗi test case sẽ chạy trong 1 transaction và tự động rollback sau khi test xong -> Giữ DB luôn sạch
@Transactional
public class InternshipPhaseControllerTest {

    // [LẤY TỪ ĐÂU]: Được Spring Boot tự động inject nhờ annotation @Autowired và @AutoConfigureMockMvc
    // [CÁCH DÙNG]: mockMvc.perform(...) để giả lập gửi các request HTTP (GET, POST, PUT, DELETE) tới API Controller
    @Autowired
    private MockMvc mockMvc;

    // [LẤY TỪ ĐÂU]: Interface InternshipPhaseRepository kéo từ package repository
    // [CÁCH DÙNG]: Dùng các hàm sẵn có như .save(), .deleteAll(), .findById() để thao tác với bảng đợt thực tập
    @Autowired
    private InternshipPhaseRepository phaseRepository;

    // [LẤY TỪ ĐÂU]: Interface InternshipAssignmentRepository từ package repository
    // [CÁCH DÙNG]: Thao tác với bảng phân công thực tập (ví dụ: .deleteAll(), .save(), .existsByPhasePhaseId())
    @Autowired
    private InternshipAssignmentRepository assignmentRepository;

    // [LẤY TỪ ĐÂU]: Interface StudentRepository từ package repository
    // [CÁCH DÙNG]: Quản lý dữ liệu sinh viên (lưu sinh viên test, dọn dẹp DB)
    @Autowired
    private StudentRepository studentRepository;

    // [LẤY TỪ ĐÂU]: Interface MentorRepository từ package repository
    // [CÁCH DÙNG]: Quản lý dữ liệu giảng viên/mentor (lưu mentor test, dọn dẹp DB)
    @Autowired
    private MentorRepository mentorRepository;

    // [LẤY TỪ ĐÂU]: Interface UserRepository từ package repository
    // [CÁCH DÙNG]: Quản lý dữ liệu tài khoản user (tạo user student/mentor trong DB)
    @Autowired
    private UserRepository userRepository;

    // [LẤY TỪ ĐÂU]: Bean PasswordEncoder được cấu hình trong SecurityConfig (dùng BCryptPasswordEncoder)
    // [CÁCH DÙNG]: passwordEncoder.encode("123456") để băm mật khẩu thô trước khi lưu vào User entity
    @Autowired
    private PasswordEncoder passwordEncoder;

    // [LẤY TỪ ĐÂU]: Bean ObjectMapper được Spring Boot tự động tạo từ thư viện Jackson
    // [CÁCH DÙNG]: objectMapper.writeValueAsString(dto) để biến đối tượng Java DTO thành chuỗi JSON đưa vào HTTP Body
    @Autowired
    private ObjectMapper objectMapper;

    // [LẤY TỪ ĐÂU]: Annotation @BeforeEach của JUnit 5 (org.junit.jupiter.api.BeforeEach)
    // [CÁCH DÙNG]: Hàm này tự động chạy trước MỖI phương thức @Test bên dưới để xóa sạch bảng dữ liệu cũ
    @BeforeEach
    void setUp() {
        // Xóa bảng con (phân công) trước để tránh bị lỗi khóa ngoại (Foreign Key Constraint)
        assignmentRepository.deleteAll();
        studentRepository.deleteAll();
        mentorRepository.deleteAll();
        phaseRepository.deleteAll();
        userRepository.deleteAll();
    }

    // @Test: Đánh dấu phương thức này là 1 kịch bản kiểm thử của JUnit 5
    @Test
    // @WithMockUser: Giả lập user đăng nhập thành công với vai trò "ADMIN" (vượt qua bảo mật Spring Security)
    @WithMockUser(roles = "ADMIN")
    // @DisplayName: Đặt tên hiển thị tiếng Việt thân thiện trong báo cáo chạy test của IDE/Gradle
    @DisplayName("1. Happy Path: Tạo giai đoạn thực tập thành công")
    void createPhase_Success() throws Exception {
        // BƯỚC 1: Chuẩn bị dữ liệu DTO request gửi đi
        // [CÁCH DÙNG]: Sử dụng Pattern Builder sinh bởi Lombok (@Builder) trên class InternshipPhaseRequest
        // LocalDate.of(year, month, day) từ java.time.LocalDate dùng để khởi tạo ngày bắt đầu/kết thúc
        InternshipPhaseRequest request = InternshipPhaseRequest.builder()
                .phaseName("Thuc tap HK1 2025-2026")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .description("Giai doan thuc tap HK1")
                .build();

        // BƯỚC 2: Thực thi HTTP Request & Kiểm tra kết quả
        // [CÁCH DÙNG]:
        // - post("/api/phases"): Dựng HTTP POST request tới đường dẫn /api/phases (từ MockMvcRequestBuilders)
        // - .contentType(MediaType.APPLICATION_JSON): Khai báo kiểu dữ liệu gửi lên là JSON
        // - .content(objectMapper.writeValueAsString(request)): Convert DTO Java thành chuỗi JSON chèn vào Body
        // - .andExpect(status().isCreated()): Xác nhận HTTP Status code trả về phải là 201 Created
        // - .andExpect(jsonPath("$.success").value(true)): Dùng cú pháp JSONPath để xác nhận trường success == true
        // - .andExpect(jsonPath("$.data.phaseName").value(...)): Xác nhận tên đợt thực tập trong JSON trả về đúng như đã gửi
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
        // BƯỚC 1: Tạo request cố tình vi phạm quy tắc thời gian (ngày bắt đầu 31/12/2025 lại sau ngày kết thúc 01/09/2025)
        InternshipPhaseRequest request = InternshipPhaseRequest.builder()
                .phaseName("Thuc tap sai ngay")
                .startDate(LocalDate.of(2025, 12, 31))
                .endDate(LocalDate.of(2025, 9, 1))
                .build();

        // BƯỚC 2: Gửi request lên Controller và kiểm tra phản hồi lỗi
        // [CÁCH DÙNG]: .andExpect(status().isBadRequest()) xác nhận Server phản hồi lỗi 400 Bad Request
        mockMvc.perform(post("/api/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    // @WithMockUser(roles = "STUDENT"): Giả lập user đăng nhập với quyền Sinh viên (STUDENT)
    @WithMockUser(roles = "STUDENT")
    @DisplayName("3. Unauthorized/Forbidden: STUDENT không được tạo giai đoạn thực tập")
    void createPhase_ForbiddenForStudent() throws Exception {
        // BƯỚC 1: Tạo dữ liệu request hợp lệ
        InternshipPhaseRequest request = InternshipPhaseRequest.builder()
                .phaseName("Thuc tap HK1")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();

        // BƯỚC 2: Gửi request dưới quyền STUDENT
        // [CÁCH DÙNG]: .andExpect(status().isForbidden()) xác nhận hệ thống chặn quyền truy cập và trả về 403 Forbidden
        mockMvc.perform(post("/api/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("4. Conflict: Trùng tên giai đoạn thực tập")
    void createPhase_ConflictName() throws Exception {
        // BƯỚC 1: Tạo trước 1 đợt thực tập hợp lệ trong cơ sở dữ liệu bằng phaseRepository.save(...)
        InternshipPhase existing = InternshipPhase.builder()
                .phaseName("Thuc tap HK1 2025-2026")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phaseRepository.save(existing);

        // BƯỚC 2: Chuẩn bị 1 request mới có tên "Thuc tap HK1 2025-2026" giống hệt đợt vừa tạo ở trên
        InternshipPhaseRequest request = InternshipPhaseRequest.builder()
                .phaseName("Thuc tap HK1 2025-2026")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();

        // BƯỚC 3: Gửi request tạo trùng tên và kiểm tra kết quả
        // [CÁCH DÙNG]: Bắt buộc trả về HTTP 400 Bad Request và success = false do vi phạm ràng buộc tên duy nhất
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
        // BƯỚC 1: Tạo 1 khóa/đợt thực tập trống (chưa có bất kỳ phân công sinh viên nào) và lưu vào DB
        InternshipPhase phase = InternshipPhase.builder()
                .phaseName("Phase Empty Delete")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phase = phaseRepository.save(phase);

        // BƯỚC 2: Gọi API Xóa với phương thức DELETE
        // [CÁCH DÙNG]:
        // - delete("/api/phases/" + phase.getPhaseId()): Tạo HTTP DELETE request chứa ID của đợt thực tập vừa lưu
        // - .andExpect(status().isOk()): Xác nhận phản hồi HTTP 200 OK xóa thành công
        mockMvc.perform(delete("/api/phases/" + phase.getPhaseId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("6. Delete Phase Error: Thất bại khi xóa giai đoạn thực tập đang có sinh viên theo học")
    void deletePhase_HasStudents_ReturnsBadRequest() throws Exception {
        // BƯỚC 1: Khởi tạo đợt thực tập mẫu và lưu vào DB
        InternshipPhase phase = InternshipPhase.builder()
                .phaseName("Phase Active With Student")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();
        phase = phaseRepository.save(phase);

        // BƯỚC 2: Khởi tạo tài khoản User và thông tin Student
        // [CÁCH DÙNG]: User.builder() tạo user tài khoản sinh viên -> passwordEncoder.encode() băm mật khẩu -> userRepository.save() lưu DB
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

        // BƯỚC 3: Khởi tạo tài khoản User và thông tin Mentor (Giảng viên hướng dẫn)
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

        // BƯỚC 4: Tạo bản ghi Phân công (InternshipAssignment) nối Sinh viên + Mentor vào đợt thực tập này
        // [CÁCH DÙNG]: assignmentRepository.save(assignment) liên kết các entity lại với nhau trong bảng phân công
        InternshipAssignment assignment = InternshipAssignment.builder()
                .phase(phase)
                .student(student)
                .mentor(mentor)
                .build();
        assignmentRepository.save(assignment);

        // BƯỚC 5: Thử nghiệm gửi HTTP DELETE yêu cầu xóa đợt thực tập vừa có sinh viên theo học ở trên
        // [CÁCH DÙNG]:
        // - perform(delete(...)): Gửi request xóa
        // - .andExpect(status().isBadRequest()): Phải bị hệ thống từ chối và trả về HTTP 400 Bad Request
        // - .andExpect(jsonPath("$.success").value(false)): Xác nhận success = false
        // - .andExpect(jsonPath("$.message").value(...)): Kiểm tra chính xác thông báo lỗi bảo vệ dữ liệu trả về cho người dùng
        mockMvc.perform(delete("/api/phases/" + phase.getPhaseId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Không thể xóa giai đoạn thực tập đang có sinh viên theo học!"));
    }
}
