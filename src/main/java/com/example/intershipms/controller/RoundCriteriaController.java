package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.AssignCriteriaRequest;
import com.example.intershipms.dto.response.RoundCriteriaResponse;
import com.example.intershipms.entity.RoundCriteria;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.RoundCriteriaRepository;
import com.example.intershipms.service.AssessmentRoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/round_criteria")
@RequiredArgsConstructor
public class RoundCriteriaController {

    private final AssessmentRoundService roundService;
    private final RoundCriteriaRepository roundCriteriaRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<RoundCriteriaResponse>>> getAllRoundCriteria() {
        List<RoundCriteriaResponse> data = roundCriteriaRepository.findAll().stream()
                .map(rc -> RoundCriteriaResponse.builder()
                        .roundCriterionId(rc.getRoundCriterionId())
                        .roundId(rc.getRound().getRoundId())
                        .roundName(rc.getRound().getRoundName())
                        .criterionId(rc.getCriterion().getCriterionId())
                        .criterionName(rc.getCriterion().getCriterionName())
                        .maxScore(rc.getCriterion().getMaxScore())
                        .weight(rc.getWeight())
                        .build())
                .collect(Collectors.toList());

        ApiResponse<List<RoundCriteriaResponse>> response = ApiResponse.<List<RoundCriteriaResponse>>builder()
                .success(true)
                .message("Lấy danh sách tiêu chí đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> getRoundCriteriaById(@PathVariable Integer id) {
        RoundCriteria rc = roundCriteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đợt đánh giá với ID: " + id));

        RoundCriteriaResponse data = RoundCriteriaResponse.builder()
                .roundCriterionId(rc.getRoundCriterionId())
                .roundId(rc.getRound().getRoundId())
                .roundName(rc.getRound().getRoundName())
                .criterionId(rc.getCriterion().getCriterionId())
                .criterionName(rc.getCriterion().getCriterionName())
                .maxScore(rc.getCriterion().getMaxScore())
                .weight(rc.getWeight())
                .build();

        ApiResponse<RoundCriteriaResponse> response = ApiResponse.<RoundCriteriaResponse>builder()
                .success(true)
                .message("Lấy chi tiết tiêu chí đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> createRoundCriteria(
            @RequestParam Integer roundId,
            @Valid @RequestBody AssignCriteriaRequest request) {
        RoundCriteriaResponse data = roundService.assignCriteriaToRound(roundId, request);
        ApiResponse<RoundCriteriaResponse> response = ApiResponse.<RoundCriteriaResponse>builder()
                .success(true)
                .message("Gán tiêu chí vào đợt đánh giá thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> updateRoundCriteria(
            @PathVariable Integer id,
            @Valid @RequestBody AssignCriteriaRequest request) {
        RoundCriteria rc = roundCriteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đợt đánh giá với ID: " + id));

        rc.setWeight(request.getWeight());
        RoundCriteria saved = roundCriteriaRepository.save(rc);

        RoundCriteriaResponse data = RoundCriteriaResponse.builder()
                .roundCriterionId(saved.getRoundCriterionId())
                .roundId(saved.getRound().getRoundId())
                .roundName(saved.getRound().getRoundName())
                .criterionId(saved.getCriterion().getCriterionId())
                .criterionName(saved.getCriterion().getCriterionName())
                .maxScore(saved.getCriterion().getMaxScore())
                .weight(saved.getWeight())
                .build();

        ApiResponse<RoundCriteriaResponse> response = ApiResponse.<RoundCriteriaResponse>builder()
                .success(true)
                .message("Cập nhật trọng số tiêu chí thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteRoundCriteria(@PathVariable Integer id) {
        RoundCriteria rc = roundCriteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đợt đánh giá với ID: " + id));

        roundCriteriaRepository.delete(rc);
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Xóa tiêu chí khỏi đợt đánh giá thành công")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
