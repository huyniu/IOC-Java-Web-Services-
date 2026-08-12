package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.EvaluationCriteriaRequest;
import com.example.intershipms.dto.request.EvaluationCriteriaUpdateRequest;
import com.example.intershipms.dto.response.EvaluationCriteriaResponse;
import com.example.intershipms.service.EvaluationCriteriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/criteria", "/api/evaluation_criteria"})
@RequiredArgsConstructor
public class EvaluationCriteriaController {

    private final EvaluationCriteriaService criteriaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> createCriteria(@Valid @RequestBody EvaluationCriteriaRequest request) {
        EvaluationCriteriaResponse data = criteriaService.createCriteria(request);
        ApiResponse<EvaluationCriteriaResponse> response = ApiResponse.<EvaluationCriteriaResponse>builder()
                .success(true)
                .message("Tạo tiêu chí đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getAllCriteria() {
        List<EvaluationCriteriaResponse> data = criteriaService.getAllCriteria();
        ApiResponse<List<EvaluationCriteriaResponse>> response = ApiResponse.<List<EvaluationCriteriaResponse>>builder()
                .success(true)
                .message("Lấy danh sách tiêu chí đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> getCriteriaById(@PathVariable Integer id) {
        EvaluationCriteriaResponse data = criteriaService.getCriteriaById(id);
        ApiResponse<EvaluationCriteriaResponse> response = ApiResponse.<EvaluationCriteriaResponse>builder()
                .success(true)
                .message("Lấy thông tin tiêu chí đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> updateCriteria(
            @PathVariable Integer id,
            @Valid @RequestBody EvaluationCriteriaUpdateRequest request) {
        EvaluationCriteriaResponse data = criteriaService.updateCriteria(id, request);
        ApiResponse<EvaluationCriteriaResponse> response = ApiResponse.<EvaluationCriteriaResponse>builder()
                .success(true)
                .message("Cập nhật tiêu chí đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteCriteria(@PathVariable Integer id) {
        criteriaService.deleteCriteria(id);
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Xóa tiêu chí đánh giá thành công")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
