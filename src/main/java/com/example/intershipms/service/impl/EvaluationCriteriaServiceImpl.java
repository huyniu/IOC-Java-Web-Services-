package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.EvaluationCriteriaRequest;
import com.example.intershipms.dto.request.EvaluationCriteriaUpdateRequest;
import com.example.intershipms.dto.response.EvaluationCriteriaResponse;
import com.example.intershipms.entity.EvaluationCriteria;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.EvaluationCriteriaRepository;
import com.example.intershipms.service.EvaluationCriteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationCriteriaServiceImpl implements EvaluationCriteriaService {

    private final EvaluationCriteriaRepository criteriaRepository;

    @Override
    public EvaluationCriteriaResponse createCriteria(EvaluationCriteriaRequest request) {
        if (criteriaRepository.existsByCriterionName(request.getCriterionName())) {
            throw new BadRequestException("Tên tiêu chí đánh giá đã tồn tại!");
        }

        EvaluationCriteria criteria = EvaluationCriteria.builder()
                .criterionName(request.getCriterionName())
                .description(request.getDescription())
                .maxScore(request.getMaxScore())
                .build();

        EvaluationCriteria savedCriteria = criteriaRepository.save(criteria);
        return mapToResponse(savedCriteria);
    }

    @Override
    public List<EvaluationCriteriaResponse> getAllCriteria() {
        return criteriaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EvaluationCriteriaResponse getCriteriaById(Integer id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đánh giá với ID: " + id));
        return mapToResponse(criteria);
    }

    @Override
    public EvaluationCriteriaResponse updateCriteria(Integer id, EvaluationCriteriaUpdateRequest request) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đánh giá với ID: " + id));

        if (criteriaRepository.existsByCriterionNameAndCriterionIdNot(request.getCriterionName(), id)) {
            throw new BadRequestException("Tên tiêu chí đánh giá đã trùng với tiêu chí khác!");
        }

        criteria.setCriterionName(request.getCriterionName());
        criteria.setDescription(request.getDescription());
        criteria.setMaxScore(request.getMaxScore());

        EvaluationCriteria updatedCriteria = criteriaRepository.save(criteria);
        return mapToResponse(updatedCriteria);
    }

    @Override
    public void deleteCriteria(Integer id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đánh giá với ID: " + id));
        criteriaRepository.delete(criteria);
    }

    private EvaluationCriteriaResponse mapToResponse(EvaluationCriteria criteria) {
        return EvaluationCriteriaResponse.builder()
                .criterionId(criteria.getCriterionId())
                .criterionName(criteria.getCriterionName())
                .description(criteria.getDescription())
                .maxScore(criteria.getMaxScore())
                .createdAt(criteria.getCreatedAt())
                .updatedAt(criteria.getUpdatedAt())
                .build();
    }
}
