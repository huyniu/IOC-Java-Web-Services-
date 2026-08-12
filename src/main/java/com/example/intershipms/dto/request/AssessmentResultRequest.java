package com.example.intershipms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResultRequest {

    @NotNull(message = "Mã phân công thực tập không được để trống")
    private Integer assignmentId;

    @NotNull(message = "Mã đợt đánh giá không được để trống")
    private Integer roundId;

    @NotNull(message = "Mã tiêu chí đánh giá không được để trống")
    private Integer criterionId;

    @NotNull(message = "Điểm số không được để trống")
    @DecimalMin(value = "0.00", message = "Điểm số không được nhỏ hơn 0")
    private BigDecimal score;

    private String comments;
}
