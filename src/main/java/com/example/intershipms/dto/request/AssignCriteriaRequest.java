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
public class AssignCriteriaRequest {

    @NotNull(message = "Mã tiêu chí không được để trống")
    private Integer criterionId;

    @NotNull(message = "Trọng số không được để trống")
    @DecimalMin(value = "0.01", message = "Trọng số phải lớn hơn 0")
    private BigDecimal weight;
}
