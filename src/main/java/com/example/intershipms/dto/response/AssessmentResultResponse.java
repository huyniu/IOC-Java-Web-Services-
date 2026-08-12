package com.example.intershipms.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResultResponse {

    private Integer resultId;
    private Integer assignmentId;
    private Integer roundId;
    private String roundName;
    private Integer criterionId;
    private String criterionName;
    private BigDecimal maxScore;
    private BigDecimal score;
    private String comments;
    private Integer evaluatedById;
    private String evaluatedByName;
    private LocalDateTime evaluationDate;
}
