package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.StudentRequest;
import com.example.intershipms.dto.request.StudentUpdateRequest;
import com.example.intershipms.dto.response.StudentResponse;
import com.example.intershipms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse data = studentService.createStudent(request);

        ApiResponse<StudentResponse> response = ApiResponse.<StudentResponse>builder()
                .success(true)
                .message("Tạo hồ sơ sinh viên thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        List<StudentResponse> data = studentService.getAllStudents();

        ApiResponse<List<StudentResponse>> response = ApiResponse.<List<StudentResponse>>builder()
                .success(true)
                .message("Lấy danh sách sinh viên thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Integer id) {
        StudentResponse data = studentService.getStudentById(id);

        ApiResponse<StudentResponse> response = ApiResponse.<StudentResponse>builder()
                .success(true)
                .message("Lấy thông tin sinh viên thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Integer id,
            @Valid @RequestBody StudentUpdateRequest request) {
        StudentResponse data = studentService.updateStudent(id, request);

        ApiResponse<StudentResponse> response = ApiResponse.<StudentResponse>builder()
                .success(true)
                .message("Cập nhật hồ sơ sinh viên thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);

        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Xóa hồ sơ sinh viên thành công")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}