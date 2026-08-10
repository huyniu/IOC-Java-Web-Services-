package com.example.intershipms.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class StudentResponse {
    private Integer studentId;
    private String studentCode;
    private String major;
    private String className;
    private LocalDate dateOfBirth;
    private String address;
    private String fullName;
    private String email;
    private String phoneNumber;
}