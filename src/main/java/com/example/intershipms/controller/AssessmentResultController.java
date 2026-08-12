package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.AssessmentResultRequest;
import com.example.intershipms.dto.response.AssessmentResultResponse;
import com.example.intershipms.dto.response.StudentEvaluationSummaryResponse;
import com.example.intershipms.service.AssessmentResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/assessments", "/api/assessment_results"})
@RequiredArgsConstructor
public class AssessmentResultController {

    private final AssessmentResultService resultService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<AssessmentResultResponse>>> getAllResults() {
        List<AssessmentResultResponse> data = resultService.getAllResults();
        ApiResponse<List<AssessmentResultResponse>> response = ApiResponse.<List<AssessmentResultResponse>>builder()
                .success(true)
                .message("Lấy danh sách kết quả đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse<AssessmentResultResponse>> submitAssessment(
            @Valid @RequestBody AssessmentResultRequest request,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        AssessmentResultResponse data = resultService.submitAssessment(request, currentUsername);
        ApiResponse<AssessmentResultResponse> response = ApiResponse.<AssessmentResultResponse>builder()
                .success(true)
                .message("Đánh giá sinh viên thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse<AssessmentResultResponse>> updateAssessment(
            @PathVariable Integer id,
            @Valid @RequestBody AssessmentResultRequest request,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        AssessmentResultResponse data = resultService.updateAssessment(id, request, currentUsername);
        ApiResponse<AssessmentResultResponse> response = ApiResponse.<AssessmentResultResponse>builder()
                .success(true)
                .message("Cập nhật kết quả đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<AssessmentResultResponse>>> getResultsByAssignmentId(@PathVariable Integer assignmentId) {
        List<AssessmentResultResponse> data = resultService.getResultsByAssignmentId(assignmentId);
        ApiResponse<List<AssessmentResultResponse>> response = ApiResponse.<List<AssessmentResultResponse>>builder()
                .success(true)
                .message("Lấy kết quả đánh giá theo phân công thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}/phase/{phaseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<StudentEvaluationSummaryResponse>> getStudentSummaryByPhase(
            @PathVariable Integer studentId,
            @PathVariable Integer phaseId) {
        StudentEvaluationSummaryResponse data = resultService.getStudentSummaryByPhase(studentId, phaseId);
        ApiResponse<StudentEvaluationSummaryResponse> response = ApiResponse.<StudentEvaluationSummaryResponse>builder()
                .success(true)
                .message("Lấy tổng hợp kết quả đánh giá thực tập của sinh viên thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
