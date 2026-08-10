package com.example.intershipms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentorResponse {
    private Integer mentorId;
    private String department;
    private String academicRank;
    private String fullName;
    private String email;
    private String phoneNumber;
}   