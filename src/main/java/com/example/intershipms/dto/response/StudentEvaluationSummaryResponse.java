package com.example.intershipms.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEvaluationSummaryResponse {

    private Integer studentId;
    private String studentName;
    private String studentCode;
    private Integer phaseId;
    private String phaseName;
    private BigDecimal totalWeightedScore;
    private List<AssessmentResultResponse> detailedResults;
}
