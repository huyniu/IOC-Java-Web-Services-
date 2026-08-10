package com.example.intershipms.controller;

import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.MentorRequest;
import com.example.intershipms.dto.request.MentorUpdateRequest;
import com.example.intershipms.dto.response.MentorResponse;
import com.example.intershipms.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MentorResponse>> createMentor(@Valid @RequestBody MentorRequest request) {
        MentorResponse data = mentorService.createMentor(request);
        return ResponseEntity.ok(ApiResponse.<MentorResponse>builder()
                .success(true).message("Tạo hồ sơ giáo viên thành công").data(data).timestamp(LocalDateTime.now()).build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'MENTOR')")
    public ResponseEntity<ApiResponse<List<MentorResponse>>> getAllMentors() {
        List<MentorResponse> data = mentorService.getAllMentors();
        return ResponseEntity.ok(ApiResponse.<List<MentorResponse>>builder()
                .success(true).message("Lấy danh sách giáo viên thành công").data(data).timestamp(LocalDateTime.now()).build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'MENTOR')")
    public ResponseEntity<ApiResponse<MentorResponse>> getMentorById(@PathVariable Integer id) {
        MentorResponse data = mentorService.getMentorById(id);
        return ResponseEntity.ok(ApiResponse.<MentorResponse>builder()
                .success(true).message("Lấy chi tiết giáo viên thành công").data(data).timestamp(LocalDateTime.now()).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
    public ResponseEntity<ApiResponse<MentorResponse>> updateMentor(
            @PathVariable Integer id, @Valid @RequestBody MentorUpdateRequest request) {
        MentorResponse data = mentorService.updateMentor(id, request);
        return ResponseEntity.ok(ApiResponse.<MentorResponse>builder()
                .success(true).message("Cập nhật hồ sơ giáo viên thành công").data(data).timestamp(LocalDateTime.now()).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteMentor(@PathVariable Integer id) {
        mentorService.deleteMentor(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Xóa hồ sơ giáo viên thành công").timestamp(LocalDateTime.now()).build());
    }
}