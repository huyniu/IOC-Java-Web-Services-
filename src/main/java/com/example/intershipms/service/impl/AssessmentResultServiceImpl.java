package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.AssessmentResultRequest;
import com.example.intershipms.dto.response.AssessmentResultResponse;
import com.example.intershipms.dto.response.StudentEvaluationSummaryResponse;
import com.example.intershipms.entity.*;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.*;
import com.example.intershipms.service.AssessmentResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentResultServiceImpl implements AssessmentResultService {

    private final AssessmentResultRepository resultRepository;
    private final InternshipAssignmentRepository assignmentRepository;
    private final AssessmentRoundRepository roundRepository;
    private final EvaluationCriteriaRepository criteriaRepository;
    private final RoundCriteriaRepository roundCriteriaRepository;
    private final UserRepository userRepository;

    @Override
    public AssessmentResultResponse submitAssessment(AssessmentResultRequest request, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng hiện tại!"));

        InternshipAssignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công thực tập với ID: " + request.getAssignmentId()));

        AssessmentRound round = roundRepository.findById(request.getRoundId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đánh giá với ID: " + request.getRoundId()));

        EvaluationCriteria criterion = criteriaRepository.findById(request.getCriterionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đánh giá với ID: " + request.getCriterionId()));

        if (!round.getPhase().getPhaseId().equals(assignment.getPhase().getPhaseId())) {
            throw new BadRequestException("Đợt đánh giá không thuộc giai đoạn thực tập của sinh viên!");
        }

        if (request.getScore().compareTo(criterion.getMaxScore()) > 0) {
            throw new BadRequestException("Điểm số (" + request.getScore() + ") không được vượt quá điểm tối đa (" + criterion.getMaxScore() + ")!");
        }

        AssessmentResult result = resultRepository
                .findByAssignmentAssignmentIdAndRoundRoundIdAndCriterionCriterionId(
                        request.getAssignmentId(), request.getRoundId(), request.getCriterionId())
                .orElse(AssessmentResult.builder()
                        .assignment(assignment)
                        .round(round)
                        .criterion(criterion)
                        .build());

        result.setScore(request.getScore());
        result.setComments(request.getComments());
        result.setEvaluatedBy(currentUser);
        result.setEvaluationDate(LocalDateTime.now());

        AssessmentResult savedResult = resultRepository.save(result);
        return mapToResponse(savedResult);
    }

    @Override
    public List<AssessmentResultResponse> getAllResults() {
        return resultRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AssessmentResultResponse getResultById(Integer id) {
        AssessmentResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kết quả đánh giá với ID: " + id));
        return mapToResponse(result);
    }

    @Override
    public AssessmentResultResponse updateAssessment(Integer id, AssessmentResultRequest request, String currentUsername) {
        AssessmentResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kết quả đánh giá với ID: " + id));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng hiện tại!"));

        if (request.getScore().compareTo(result.getCriterion().getMaxScore()) > 0) {
            throw new BadRequestException("Điểm số (" + request.getScore() + ") không được vượt quá điểm tối đa (" + result.getCriterion().getMaxScore() + ")!");
        }

        result.setScore(request.getScore());
        result.setComments(request.getComments());
        result.setEvaluatedBy(currentUser);
        result.setEvaluationDate(LocalDateTime.now());

        AssessmentResult updatedResult = resultRepository.save(result);
        return mapToResponse(updatedResult);
    }

    @Override
    public List<AssessmentResultResponse> getResultsByAssignmentId(Integer assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Không tìm thấy phân công thực tập với ID: " + assignmentId);
        }
        return resultRepository.findByAssignmentAssignmentId(assignmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentEvaluationSummaryResponse getStudentSummaryByPhase(Integer studentId, Integer phaseId) {
        InternshipAssignment assignment = assignmentRepository.findByStudentStudentIdAndPhasePhaseId(studentId, phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thực tập của sinh viên trong giai đoạn này!"));

        List<AssessmentResult> results = resultRepository.findByAssignmentStudentStudentIdAndAssignmentPhasePhaseId(studentId, phaseId);

        BigDecimal totalWeightedScore = BigDecimal.ZERO;
        for (AssessmentResult res : results) {
            RoundCriteria rc = roundCriteriaRepository
                    .findByRoundRoundIdAndCriterionCriterionId(res.getRound().getRoundId(), res.getCriterion().getCriterionId())
                    .orElse(null);

            BigDecimal weight = (rc != null) ? rc.getWeight() : BigDecimal.ONE;
            BigDecimal itemWeightedScore = res.getScore().multiply(weight);
            totalWeightedScore = totalWeightedScore.add(itemWeightedScore);
        }
        totalWeightedScore = totalWeightedScore.setScale(2, RoundingMode.HALF_UP);

        List<AssessmentResultResponse> detailedResults = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return StudentEvaluationSummaryResponse.builder()
                .studentId(studentId)
                .studentName(assignment.getStudent().getUser().getFullName())
                .studentCode(assignment.getStudent().getStudentCode())
                .phaseId(phaseId)
                .phaseName(assignment.getPhase().getPhaseName())
                .totalWeightedScore(totalWeightedScore)
                .detailedResults(detailedResults)
                .build();
    }

    private AssessmentResultResponse mapToResponse(AssessmentResult result) {
        return AssessmentResultResponse.builder()
                .resultId(result.getResultId())
                .assignmentId(result.getAssignment().getAssignmentId())
                .roundId(result.getRound().getRoundId())
                .roundName(result.getRound().getRoundName())
                .criterionId(result.getCriterion().getCriterionId())
                .criterionName(result.getCriterion().getCriterionName())
                .maxScore(result.getCriterion().getMaxScore())
                .score(result.getScore())
                .comments(result.getComments())
                .evaluatedById(result.getEvaluatedBy().getUserId())
                .evaluatedByName(result.getEvaluatedBy().getFullName())
                .evaluationDate(result.getEvaluationDate())
                .build();
    }
}
