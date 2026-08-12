package com.example.intershipms.dto.request;

import com.example.intershipms.entity.InternshipAssignment.AssignmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentStatusUpdateRequest {

    @NotNull(message = "Trạng thái phân công không được để trống")
    private AssignmentStatus status;
}
