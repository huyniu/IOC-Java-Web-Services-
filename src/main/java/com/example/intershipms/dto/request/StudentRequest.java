package com.example.intershipms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentRequest {
    @NotNull(message = "ID của tài khoản (User) không được để trống")
    private Integer userId;

    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @NotBlank(message = "Chuyên ngành không được để trống")
    private String major;

    @NotBlank(message = "Lớp học không được để trống")
    private String className;

    private LocalDate dateOfBirth;

    private String address;
}