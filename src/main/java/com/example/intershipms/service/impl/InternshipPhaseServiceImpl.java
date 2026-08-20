package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.InternshipPhaseRequest;
import com.example.intershipms.dto.request.InternshipPhaseUpdateRequest;
import com.example.intershipms.dto.response.InternshipPhaseResponse;
import com.example.intershipms.entity.InternshipPhase;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.InternshipAssignmentRepository;
import com.example.intershipms.repository.InternshipPhaseRepository;
import com.example.intershipms.service.InternshipPhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipPhaseServiceImpl implements InternshipPhaseService {

    // Kho lưu trữ dữ liệu cho khóa/đợt thực tập
    private final InternshipPhaseRepository phaseRepository;
    // Kho lưu trữ dữ liệu phân công sinh viên thực tập
    private final InternshipAssignmentRepository assignmentRepository;

    /**
     * Tạo một khóa/đợt thực tập mới
     */
    @Override
    public InternshipPhaseResponse createPhase(InternshipPhaseRequest request) {
        // Kiểm tra logic thời gian: Ngày bắt đầu không thể diễn ra sau ngày kết thúc
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc!");
        }

        // Đảm bảo tên đợt thực tập là duy nhất, không trùng với đợt khác
        if (phaseRepository.existsByPhaseName(request.getPhaseName())) {
            throw new BadRequestException("Tên giai đoạn thực tập đã tồn tại!");
        }

        // Đóng gói thông tin khóa thực tập mới
        InternshipPhase phase = InternshipPhase.builder()
                .phaseName(request.getPhaseName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .build();

        // Lưu vào cơ sở dữ liệu và trả về kết quả cho client
        InternshipPhase savedPhase = phaseRepository.save(phase);
        return mapToResponse(savedPhase);
    }

    /**
     * Lấy danh sách tất cả các khóa/đợt thực tập hiện có
     */
    @Override
    public List<InternshipPhaseResponse> getAllPhases() {
        return phaseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tìm chi tiết một khóa thực tập theo ID
     */
    @Override
    public InternshipPhaseResponse getPhaseById(Integer id) {
        // Báo lỗi ngay nếu không tìm thấy đợt thực tập với ID yêu cầu
        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập với ID: " + id));
        return mapToResponse(phase);
    }

    /**
     * Cập nhật thông tin cho khóa/đợt thực tập
     */
    @Override
    public InternshipPhaseResponse updatePhase(Integer id, InternshipPhaseUpdateRequest request) {
        // Tìm khóa thực tập cần sửa
        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập với ID: " + id));

        // Kiểm tra lại logic ngày tháng
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc!");
        }

        // Kiểm tra xem tên mới có bị trùng với khóa thực tập nào khác không
        if (phaseRepository.existsByPhaseNameAndPhaseIdNot(request.getPhaseName(), id)) {
            throw new BadRequestException("Tên giai đoạn thực tập đã trùng với một giai đoạn khác!");
        }

        // Cập nhật các thông tin mới
        phase.setPhaseName(request.getPhaseName());
        phase.setStartDate(request.getStartDate());
        phase.setEndDate(request.getEndDate());
        phase.setDescription(request.getDescription());

        // Lưu bản ghi đã cập nhật
        InternshipPhase updatedPhase = phaseRepository.save(phase);
        return mapToResponse(updatedPhase);
    }

    /**
     * Xóa khóa/đợt thực tập
     */
    @Override
    public void deletePhase(Integer id) {
        // 1. Kiểm tra xem đợt thực tập có tồn tại không
        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập với ID: " + id));

        // 2. Bảo vệ dữ liệu: Nếu khóa học đang có sinh viên đăng ký/theo học thì không cho phép xóa
        if (assignmentRepository.existsByPhasePhaseId(id)) {
            throw new BadRequestException("Không thể xóa giai đoạn thực tập đang có sinh viên theo học!");
        }

        // 3. Tiến hành xóa nếu thỏa mãn mọi điều kiện an toàn
        phaseRepository.delete(phase);
    }

    /**
     * Hàm tiện ích: Chuyển đổi từ Entity sang DTO Response để trả về cho Client
     */
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
