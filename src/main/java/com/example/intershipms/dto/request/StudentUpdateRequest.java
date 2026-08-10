package com.example.intershipms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentUpdateRequest {
    @NotBlank(message = "Chuyên ngành không được để trống")
    private String major;

    @NotBlank(message = "Lớp học không được để trống")
    private String className;

    private LocalDate dateOfBirth;
    private String address;
}