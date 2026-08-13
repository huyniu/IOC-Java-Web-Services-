package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.AssessmentRoundRequest;
import com.example.intershipms.dto.request.AssessmentRoundUpdateRequest;
import com.example.intershipms.dto.request.AssignCriteriaRequest;
import com.example.intershipms.dto.response.AssessmentRoundResponse;
import com.example.intershipms.dto.response.RoundCriteriaResponse;
import com.example.intershipms.entity.AssessmentRound;
import com.example.intershipms.entity.EvaluationCriteria;
import com.example.intershipms.entity.InternshipPhase;
import com.example.intershipms.entity.RoundCriteria;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.AssessmentRoundRepository;
import com.example.intershipms.repository.EvaluationCriteriaRepository;
import com.example.intershipms.repository.InternshipPhaseRepository;
import com.example.intershipms.repository.RoundCriteriaRepository;
import com.example.intershipms.service.AssessmentRoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentRoundServiceImpl implements AssessmentRoundService {

    private final AssessmentRoundRepository roundRepository;
    private final InternshipPhaseRepository phaseRepository;
    private final EvaluationCriteriaRepository criteriaRepository;
    private final RoundCriteriaRepository roundCriteriaRepository;

    @Override
    public AssessmentRoundResponse createRound(AssessmentRoundRequest request) {
        InternshipPhase phase = phaseRepository.findById(request.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập với ID: " + request.getPhaseId()));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc!");
        }

        if (roundRepository.existsByPhasePhaseIdAndRoundName(request.getPhaseId(), request.getRoundName())) {
            throw new BadRequestException("Đợt đánh giá với tên này đã tồn tại trong giai đoạn!");
        }

        AssessmentRound round = AssessmentRound.builder()
                .phase(phase)
                .roundName(request.getRoundName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        AssessmentRound savedRound = roundRepository.save(round);
        return mapToResponse(savedRound);
    }

    @Override
    public List<AssessmentRoundResponse> getAllRounds(Long phaseId) {
        List<AssessmentRound> rounds;
        if (phaseId != null) {
            rounds = roundRepository.findByPhaseId(phaseId);
        } else {
            rounds = roundRepository.findAll();
        }
        return rounds.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AssessmentRoundResponse getRoundById(Integer id) {
        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đánh giá với ID: " + id));
        return mapToResponse(round);
    }

    @Override
    public AssessmentRoundResponse updateRound(Integer id, AssessmentRoundUpdateRequest request) {
        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đánh giá với ID: " + id));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc!");
        }

        if (roundRepository.existsByPhasePhaseIdAndRoundNameAndRoundIdNot(round.getPhase().getPhaseId(), request.getRoundName(), id)) {
            throw new BadRequestException("Tên đợt đánh giá đã tồn tại trong giai đoạn này!");
        }

        round.setRoundName(request.getRoundName());
        round.setStartDate(request.getStartDate());
        round.setEndDate(request.getEndDate());
        round.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            round.setIsActive(request.getIsActive());
        }

        AssessmentRound updatedRound = roundRepository.save(round);
        return mapToResponse(updatedRound);
    }

    @Override
    public void deleteRound(Integer id) {
        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đánh giá với ID: " + id));
        roundRepository.delete(round);
    }

    @Override
    public RoundCriteriaResponse assignCriteriaToRound(Integer roundId, AssignCriteriaRequest request) {
        AssessmentRound round = roundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đánh giá với ID: " + roundId));

        EvaluationCriteria criteria = criteriaRepository.findById(request.getCriterionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đánh giá với ID: " + request.getCriterionId()));

        RoundCriteria roundCriteria = roundCriteriaRepository.findByRoundRoundIdAndCriterionCriterionId(roundId, request.getCriterionId())
                .orElse(RoundCriteria.builder()
                        .round(round)
                        .criterion(criteria)
                        .build());

        roundCriteria.setWeight(request.getWeight());
        RoundCriteria saved = roundCriteriaRepository.save(roundCriteria);

        return RoundCriteriaResponse.builder()
                .roundCriterionId(saved.getRoundCriterionId())
                .roundId(round.getRoundId())
                .roundName(round.getRoundName())
                .criterionId(criteria.getCriterionId())
                .criterionName(criteria.getCriterionName())
                .maxScore(criteria.getMaxScore())
                .weight(saved.getWeight())
                .build();
    }

    @Override
    public List<RoundCriteriaResponse> getCriteriaByRoundId(Integer roundId) {
        if (!roundRepository.existsById(roundId)) {
            throw new ResourceNotFoundException("Không tìm thấy đợt đánh giá với ID: " + roundId);
        }

        return roundCriteriaRepository.findByRoundRoundId(roundId).stream()
                .map(rc -> RoundCriteriaResponse.builder()
                        .roundCriterionId(rc.getRoundCriterionId())
                        .roundId(rc.getRound().getRoundId())
                        .roundName(rc.getRound().getRoundName())
                        .criterionId(rc.getCriterion().getCriterionId())
                        .criterionName(rc.getCriterion().getCriterionName())
                        .maxScore(rc.getCriterion().getMaxScore())
                        .weight(rc.getWeight())
                        .build())
                .collect(Collectors.toList());
    }

    private AssessmentRoundResponse mapToResponse(AssessmentRound round) {
        return AssessmentRoundResponse.builder()
                .roundId(round.getRoundId())
                .phaseId(round.getPhase().getPhaseId())
                .phaseName(round.getPhase().getPhaseName())
                .roundName(round.getRoundName())
                .startDate(round.getStartDate())
                .endDate(round.getEndDate())
                .description(round.getDescription())
                .isActive(round.getIsActive())
                .createdAt(round.getCreatedAt())
                .updatedAt(round.getUpdatedAt())
                .build();
    }
}
