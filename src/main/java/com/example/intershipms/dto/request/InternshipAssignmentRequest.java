package com.example.intershipms.dto.request;

import com.example.intershipms.entity.InternshipAssignment.AssignmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAssignmentRequest {

    @NotNull(message = "Mã sinh viên không được để trống")
    private Integer studentId;

    @NotNull(message = "Mã giảng viên hướng dẫn không được để trống")
    private Integer mentorId;

    @NotNull(message = "Mã giai đoạn thực tập không được để trống")
    private Integer phaseId;

    private AssignmentStatus status;
}
