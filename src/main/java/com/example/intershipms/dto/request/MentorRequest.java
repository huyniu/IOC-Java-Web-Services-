package com.example.intershipms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorRequest {

    @NotNull(message = "ID của tài khoản (User) không được để trống")
    private Integer userId;

    @NotBlank(message = "Bộ môn/Khoa không được để trống")
    private String department;

    private String academicRank;
}