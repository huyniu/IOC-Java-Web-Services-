package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.InternshipPhaseRequest;
import com.example.intershipms.dto.request.InternshipPhaseUpdateRequest;
import com.example.intershipms.dto.response.InternshipPhaseResponse;
import com.example.intershipms.service.InternshipPhaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/phases", "/api/internship_phases"})
@RequiredArgsConstructor
public class InternshipPhaseController {

    private final InternshipPhaseService phaseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> createPhase(@Valid @RequestBody InternshipPhaseRequest request) {
        InternshipPhaseResponse data = phaseService.createPhase(request);
        ApiResponse<InternshipPhaseResponse> response = ApiResponse.<InternshipPhaseResponse>builder()
                .success(true)
                .message("Tạo giai đoạn thực tập thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<InternshipPhaseResponse>>> getAllPhases() {
        List<InternshipPhaseResponse> data = phaseService.getAllPhases();
        ApiResponse<List<InternshipPhaseResponse>> response = ApiResponse.<List<InternshipPhaseResponse>>builder()
                .success(true)
                .message("Lấy danh sách giai đoạn thực tập thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> getPhaseById(@PathVariable Integer id) {
        InternshipPhaseResponse data = phaseService.getPhaseById(id);
        ApiResponse<InternshipPhaseResponse> response = ApiResponse.<InternshipPhaseResponse>builder()
                .success(true)
                .message("Lấy thông tin giai đoạn thực tập thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> updatePhase(
            @PathVariable Integer id,
            @Valid @RequestBody InternshipPhaseUpdateRequest request) {
        InternshipPhaseResponse data = phaseService.updatePhase(id, request);
        ApiResponse<InternshipPhaseResponse> response = ApiResponse.<InternshipPhaseResponse>builder()
                .success(true)
                .message("Cập nhật giai đoạn thực tập thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deletePhase(@PathVariable Integer id) {
        phaseService.deletePhase(id);
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Xóa giai đoạn thực tập thành công")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
