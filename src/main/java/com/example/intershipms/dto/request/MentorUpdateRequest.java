package com.example.intershipms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MentorUpdateRequest {
    @NotBlank(message = "Bộ môn/Khoa không được để trống")
    private String department;

    private String academicRank;
}