package com.example.intershipms.dto.response;

import com.example.intershipms.entity.InternshipAssignment.AssignmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAssignmentResponse {

    private Integer assignmentId;
    private Integer studentId;
    private String studentName;
    private String studentCode;
    private Integer mentorId;
    private String mentorName;
    private Integer phaseId;
    private String phaseName;
    private LocalDateTime assignedDate;
    private AssignmentStatus status;
}
