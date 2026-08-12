package com.example.intershipms.service;

import com.example.intershipms.dto.request.EvaluationCriteriaRequest;
import com.example.intershipms.dto.request.EvaluationCriteriaUpdateRequest;
import com.example.intershipms.dto.response.EvaluationCriteriaResponse;

import java.util.List;

public interface EvaluationCriteriaService {
    EvaluationCriteriaResponse createCriteria(EvaluationCriteriaRequest request);
    List<EvaluationCriteriaResponse> getAllCriteria();
    EvaluationCriteriaResponse getCriteriaById(Integer id);
    EvaluationCriteriaResponse updateCriteria(Integer id, EvaluationCriteriaUpdateRequest request);
    void deleteCriteria(Integer id);
}
