package com.example.intershipms.service;

import com.example.intershipms.dto.request.AssessmentRoundRequest;
import com.example.intershipms.dto.request.AssessmentRoundUpdateRequest;
import com.example.intershipms.dto.request.AssignCriteriaRequest;
import com.example.intershipms.dto.response.AssessmentRoundResponse;
import com.example.intershipms.dto.response.RoundCriteriaResponse;

import java.util.List;

public interface AssessmentRoundService {
    AssessmentRoundResponse createRound(AssessmentRoundRequest request);
    List<AssessmentRoundResponse> getAllRounds(Long phaseId);
    default List<AssessmentRoundResponse> getAllRounds() {
        return getAllRounds(null);
    }
    AssessmentRoundResponse getRoundById(Integer id);
    AssessmentRoundResponse updateRound(Integer id, AssessmentRoundUpdateRequest request);
    void deleteRound(Integer id);

    RoundCriteriaResponse assignCriteriaToRound(Integer roundId, AssignCriteriaRequest request);
    List<RoundCriteriaResponse> getCriteriaByRoundId(Integer roundId);
    List<RoundCriteriaResponse> getAllRoundCriteria(Long roundId);
}
