package com.example.intershipms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentRoundRequest {

    @NotNull(message = "Mã giai đoạn thực tập không được để trống")
    private Integer phaseId;

    @NotBlank(message = "Tên đợt đánh giá không được để trống")
    private String roundName;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    private String description;

    @Builder.Default
    private Boolean isActive = true;
}
