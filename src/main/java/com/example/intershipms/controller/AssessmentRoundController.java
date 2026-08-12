package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.AssessmentRoundRequest;
import com.example.intershipms.dto.request.AssessmentRoundUpdateRequest;
import com.example.intershipms.dto.request.AssignCriteriaRequest;
import com.example.intershipms.dto.response.AssessmentRoundResponse;
import com.example.intershipms.dto.response.RoundCriteriaResponse;
import com.example.intershipms.service.AssessmentRoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/rounds", "/api/assessment_rounds"})
@RequiredArgsConstructor
public class AssessmentRoundController {

    private final AssessmentRoundService roundService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> createRound(@Valid @RequestBody AssessmentRoundRequest request) {
        AssessmentRoundResponse data = roundService.createRound(request);
        ApiResponse<AssessmentRoundResponse> response = ApiResponse.<AssessmentRoundResponse>builder()
                .success(true)
                .message("Tạo đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<AssessmentRoundResponse>>> getAllRounds() {
        List<AssessmentRoundResponse> data = roundService.getAllRounds();
        ApiResponse<List<AssessmentRoundResponse>> response = ApiResponse.<List<AssessmentRoundResponse>>builder()
                .success(true)
                .message("Lấy danh sách đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> getRoundById(@PathVariable Integer id) {
        AssessmentRoundResponse data = roundService.getRoundById(id);
        ApiResponse<AssessmentRoundResponse> response = ApiResponse.<AssessmentRoundResponse>builder()
                .success(true)
                .message("Lấy thông tin đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> updateRound(
            @PathVariable Integer id,
            @Valid @RequestBody AssessmentRoundUpdateRequest request) {
        AssessmentRoundResponse data = roundService.updateRound(id, request);
        ApiResponse<AssessmentRoundResponse> response = ApiResponse.<AssessmentRoundResponse>builder()
                .success(true)
                .message("Cập nhật đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteRound(@PathVariable Integer id) {
        roundService.deleteRound(id);
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Xóa đợt đánh giá thành công")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roundId}/criteria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> assignCriteriaToRound(
            @PathVariable Integer roundId,
            @Valid @RequestBody AssignCriteriaRequest request) {
        RoundCriteriaResponse data = roundService.assignCriteriaToRound(roundId, request);
        ApiResponse<RoundCriteriaResponse> response = ApiResponse.<RoundCriteriaResponse>builder()
                .success(true)
                .message("Gán tiêu chí vào đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roundId}/criteria")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<RoundCriteriaResponse>>> getCriteriaByRoundId(@PathVariable Integer roundId) {
        List<RoundCriteriaResponse> data = roundService.getCriteriaByRoundId(roundId);
        ApiResponse<List<RoundCriteriaResponse>> response = ApiResponse.<List<RoundCriteriaResponse>>builder()
                .success(true)
                .message("Lấy danh sách tiêu chí của đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
