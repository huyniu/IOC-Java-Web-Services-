package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.AssignmentStatusUpdateRequest;
import com.example.intershipms.dto.request.InternshipAssignmentRequest;
import com.example.intershipms.dto.response.InternshipAssignmentResponse;
import com.example.intershipms.service.InternshipAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/assignments", "/api/internship_assignments"})
@RequiredArgsConstructor
public class InternshipAssignmentController {

    private final InternshipAssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InternshipAssignmentResponse>> assignStudent(@Valid @RequestBody InternshipAssignmentRequest request) {
        InternshipAssignmentResponse data = assignmentService.assignStudent(request);
        ApiResponse<InternshipAssignmentResponse> response = ApiResponse.<InternshipAssignmentResponse>builder()
                .success(true)
                .message("Phân công thực tập thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<InternshipAssignmentResponse>>> getAllAssignments() {
        List<InternshipAssignmentResponse> data = assignmentService.getAllAssignments();
        ApiResponse<List<InternshipAssignmentResponse>> response = ApiResponse.<List<InternshipAssignmentResponse>>builder()
                .success(true)
                .message("Lấy danh sách phân công thực tập thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<InternshipAssignmentResponse>> getAssignmentById(@PathVariable Integer id) {
        InternshipAssignmentResponse data = assignmentService.getAssignmentById(id);
        ApiResponse<InternshipAssignmentResponse> response = ApiResponse.<InternshipAssignmentResponse>builder()
                .success(true)
                .message("Lấy chi tiết phân công thực tập thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InternshipAssignmentResponse>> updateAssignmentStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AssignmentStatusUpdateRequest request) {
        InternshipAssignmentResponse data = assignmentService.updateAssignmentStatus(id, request);
        ApiResponse<InternshipAssignmentResponse> response = ApiResponse.<InternshipAssignmentResponse>builder()
                .success(true)
                .message("Cập nhật trạng thái phân công thực tập thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mentor/{mentorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<ApiResponse<List<InternshipAssignmentResponse>>> getAssignmentsByMentorId(@PathVariable Integer mentorId) {
        List<InternshipAssignmentResponse> data = assignmentService.getAssignmentsByMentorId(mentorId);
        ApiResponse<List<InternshipAssignmentResponse>> response = ApiResponse.<List<InternshipAssignmentResponse>>builder()
                .success(true)
                .message("Lấy danh sách sinh viên được phân công cho giảng viên thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<InternshipAssignmentResponse>>> getAssignmentsByStudentId(@PathVariable Integer studentId) {
        List<InternshipAssignmentResponse> data = assignmentService.getAssignmentsByStudentId(studentId);
        ApiResponse<List<InternshipAssignmentResponse>> response = ApiResponse.<List<InternshipAssignmentResponse>>builder()
                .success(true)
                .message("Lấy danh sách phân công thực tập của sinh viên thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
