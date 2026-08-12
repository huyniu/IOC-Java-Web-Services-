package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.InternshipPhaseRequest;
import com.example.intershipms.dto.request.InternshipPhaseUpdateRequest;
import com.example.intershipms.dto.response.InternshipPhaseResponse;
import com.example.intershipms.entity.InternshipPhase;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.InternshipPhaseRepository;
import com.example.intershipms.service.InternshipPhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipPhaseServiceImpl implements InternshipPhaseService {

    private final InternshipPhaseRepository phaseRepository;

    @Override
    public InternshipPhaseResponse createPhase(InternshipPhaseRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc!");
        }

        if (phaseRepository.existsByPhaseName(request.getPhaseName())) {
            throw new BadRequestException("Tên giai đoạn thực tập đã tồn tại!");
        }

        InternshipPhase phase = InternshipPhase.builder()
                .phaseName(request.getPhaseName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .build();

        InternshipPhase savedPhase = phaseRepository.save(phase);
        return mapToResponse(savedPhase);
    }

    @Override
    public List<InternshipPhaseResponse> getAllPhases() {
        return phaseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InternshipPhaseResponse getPhaseById(Integer id) {
        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập với ID: " + id));
        return mapToResponse(phase);
    }

    @Override
    public InternshipPhaseResponse updatePhase(Integer id, InternshipPhaseUpdateRequest request) {
        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập với ID: " + id));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc!");
        }

        if (phaseRepository.existsByPhaseNameAndPhaseIdNot(request.getPhaseName(), id)) {
            throw new BadRequestException("Tên giai đoạn thực tập đã trùng với một giai đoạn khác!");
        }

        phase.setPhaseName(request.getPhaseName());
        phase.setStartDate(request.getStartDate());
        phase.setEndDate(request.getEndDate());
        phase.setDescription(request.getDescription());

        InternshipPhase updatedPhase = phaseRepository.save(phase);
        return mapToResponse(updatedPhase);
    }

    @Override
    public void deletePhase(Integer id) {
        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập với ID: " + id));
        phaseRepository.delete(phase);
    }

    private InternshipPhaseResponse mapToResponse(InternshipPhase phase) {
        return InternshipPhaseResponse.builder()
                .phaseId(phase.getPhaseId())
                .phaseName(phase.getPhaseName())
                .startDate(phase.getStartDate())
                .endDate(phase.getEndDate())
                .description(phase.getDescription())
                .createdAt(phase.getCreatedAt())
                .updatedAt(phase.getUpdatedAt())
                .build();
    }
}
