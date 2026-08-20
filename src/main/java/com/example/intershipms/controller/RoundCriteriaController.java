package com.example.intershipms.controller;

// Lấy các lớp DTO để nhận dữ liệu gửi lên và trả kết quả về cho client
import com.example.intershipms.dto.ApiResponse;
import com.example.intershipms.dto.request.AssignCriteriaRequest;
import com.example.intershipms.dto.request.RoundCriteriaRequest;
import com.example.intershipms.dto.response.RoundCriteriaResponse;
// Lấy Entity và ngoại lệ báo lỗi khi không tìm thấy dữ liệu
import com.example.intershipms.entity.RoundCriteria;
import com.example.intershipms.exception.ResourceNotFoundException;
// Lấy Repository (để làm việc với CSDL) và Service (chứa logic nghiệp vụ)
import com.example.intershipms.repository.RoundCriteriaRepository;
import com.example.intershipms.service.AssessmentRoundService;
// Các thư viện hỗ trợ kiểm tra dữ liệu, phân quyền và tạo API của Spring
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Nơi tiếp nhận và xử lý các yêu cầu (API) liên quan đến Tiêu chí trong từng Đợt đánh giá.
 */
@RestController // Báo cho Spring biết đây là bộ điều khiển API, dữ liệu trả về sẽ tự động biến thành dạng JSON
@RequestMapping("/api/round_criteria") // Gom tất cả đường dẫn API trong file này bắt đầu bằng /api/round_criteria
@RequiredArgsConstructor // Tự động kết nối các Service và Repository ở bên dưới mà không cần viết code tiêm thủ công
public class RoundCriteriaController {

    // Khai báo Service để nhờ xử lý các logic phức tạp
    private final AssessmentRoundService roundService;
    // Khai báo Repository để tự tìm hoặc lưu trực tiếp vào bảng round_criteria trong CSDL
    private final RoundCriteriaRepository roundCriteriaRepository;

    /**
     * API 1: Lấy danh sách các tiêu chí trong đợt đánh giá (có thể lọc theo đợt cụ thể nếu muốn).
     */
    @GetMapping // Chờ nhận yêu cầu xem/lấy dữ liệu (HTTP GET)
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')") // Khóa bảo vệ: Admin, Mentor hay Sinh viên đều xem được
    public ResponseEntity<ApiResponse<List<RoundCriteriaResponse>>> getAllRoundCriteria(
            @RequestParam(name = "round_id", required = false) Long roundId) { // Hứng tham số ?round_id=... trên đường dẫn, không bắt buộc phải truyền
        
        // Nhờ Service đi gom danh sách các tiêu chí (nếu có truyền roundId thì lọc theo đợt đó)
        List<RoundCriteriaResponse> data = roundService.getAllRoundCriteria(roundId);

        // Đóng gói kết quả trả về cho đẹp đẽ và rõ ràng
        ApiResponse<List<RoundCriteriaResponse>> response = ApiResponse.<List<RoundCriteriaResponse>>builder()
                .success(true) // Đánh dấu là làm mọi thứ suôn sẻ
                .message("Lấy danh sách tiêu chí đợt đánh giá thành công") // Câu thông báo thân thiện
                .data(data) // Thả danh sách vừa lấy được vào đây
                .timestamp(LocalDateTime.now()) // Đóng dấu thời gian lúc trả về kết quả
                .build(); // Dán tem hoàn thành gói hàng

        // Trả về cho giao diện kèm mã trạng thái 200 (Mọi thứ OK)
        return ResponseEntity.ok(response);
    }

    /**
     * API 2: Xem chi tiết đúng 1 tiêu chí trong đợt đánh giá theo ID.
     */
    @GetMapping("/{id}") // Đường dẫn dạng: /api/round_criteria/123
    @PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')") // Admin, Mentor hay Sinh viên cũng đều xem được
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> getRoundCriteriaById(@PathVariable Integer id) { // Bắt lấy cái số id trên đường dẫn
        
        // Tìm trong CSDL xem có đúng cái ID này không, nếu không thấy thì báo lỗi ngay lập tức
        RoundCriteria rc = roundCriteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đợt đánh giá với ID: " + id));

        // Bóc tách dữ liệu từ CSDL ra để sắp xếp lại thành đối tượng đẹp đẽ gửi về cho phía giao diện (Frontend)
        RoundCriteriaResponse data = RoundCriteriaResponse.builder()
                .roundCriterionId(rc.getRoundCriterionId()) // ID của bản ghi này
                .roundId(rc.getRound().getRoundId()) // ID đợt đánh giá
                .roundName(rc.getRound().getRoundName()) // Tên đợt đánh giá
                .criterionId(rc.getCriterion().getCriterionId()) // ID tiêu chí
                .criterionName(rc.getCriterion().getCriterionName()) // Tên tiêu chí
                .maxScore(rc.getCriterion().getMaxScore()) // Điểm tối đa của tiêu chí này
                .weight(rc.getWeight()) // Trọng số (mức độ quan trọng %) của tiêu chí trong đợt này
                .build(); // Đóng gói xong

        // Tạo gói phản hồi báo thành công
        ApiResponse<RoundCriteriaResponse> response = ApiResponse.<RoundCriteriaResponse>builder()
                .success(true) // Thành công rồi nhé
                .message("Lấy chi tiết tiêu chí đợt đánh giá thành công") // Thông báo cho người dùng
                .data(data) // Gửi kèm thông tin chi tiết vừa chuẩn bị
                .timestamp(LocalDateTime.now()) // Ghi lại giờ phút hiện tại
                .build(); // Hoàn thành gói phản hồi

        // Gửi về cho Client với mã 200 OK
        return ResponseEntity.ok(response);
    }

    /**
     * API 3: Gán (thêm mới) một tiêu chí vào đợt đánh giá.
     */
    @PostMapping // Chờ nhận yêu cầu thêm mới dữ liệu (HTTP POST)
    @PreAuthorize("hasRole('ADMIN')") // Bảo mật cao: Chỉ mỗi Admin mới có quyền làm việc này!
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> createRoundCriteria(
            @Valid @RequestBody RoundCriteriaRequest request) { // Hứng dữ liệu JSON gửi lên và kiểm tra xem có hợp lệ không
        
        // Gom dữ liệu cần thiết (ID tiêu chí và trọng số) lại để gửi sang Service
        AssignCriteriaRequest assignRequest = AssignCriteriaRequest.builder()
                .criterionId(request.getCriterionId()) // Lấy ID tiêu chí người dùng chọn
                .weight(request.getWeight()) // Lấy trọng số người dùng nhập
                .build(); // Đóng gói xong request phụ

        // Gọi Service gán tiêu chí vào đợt đánh giá tương ứng
        RoundCriteriaResponse data = roundService.assignCriteriaToRound(request.getRoundId(), assignRequest);

        // Chuẩn bị câu trả lời thông báo thêm mới thành công
        ApiResponse<RoundCriteriaResponse> response = ApiResponse.<RoundCriteriaResponse>builder()
                .success(true) // Báo thành công
                .message("Gán tiêu chí vào đợt đánh giá thành công") // Thông báo kết quả
                .data(data) // Trả lại thông tin tiêu chí vừa gán xong
                .timestamp(LocalDateTime.now()) // Đánh dấu thời gian
                .build(); // Đóng gói xong

        // Trả về kèm mã 201 (CREATED - Đã tạo thành công dữ liệu mới)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API 4: Sửa lại trọng số của tiêu chí trong đợt đánh giá.
     */
    @PutMapping("/{id}") // Chờ nhận yêu cầu cập nhật/sửa đổi dữ liệu (HTTP PUT)
    @PreAuthorize("hasRole('ADMIN')") // Vẫn chỉ Admin mới được phép sửa
    public ResponseEntity<ApiResponse<RoundCriteriaResponse>> updateRoundCriteria(
            @PathVariable Integer id, // Lấy ID cần sửa trên đường dẫn
            @Valid @RequestBody AssignCriteriaRequest request) { // Lấy dữ liệu trọng số mới gửi kèm trong body
        
        // Tìm xem bản ghi này có tồn tại không, không có thì văng lỗi báo không tìm thấy ngay
        RoundCriteria rc = roundCriteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đợt đánh giá với ID: " + id));

        // Thay trọng số cũ bằng trọng số mới người dùng vừa nhập vào
        rc.setWeight(request.getWeight());
        
        // Lưu ngay bản ghi đã sửa vào CSDL
        RoundCriteria saved = roundCriteriaRepository.save(rc);

        // Soạn lại dữ liệu đã lưu thành dạng đẹp đẽ để trả về
        RoundCriteriaResponse data = RoundCriteriaResponse.builder()
                .roundCriterionId(saved.getRoundCriterionId())
                .roundId(saved.getRound().getRoundId())
                .roundName(saved.getRound().getRoundName())
                .criterionId(saved.getCriterion().getCriterionId())
                .criterionName(saved.getCriterion().getCriterionName())
                .maxScore(saved.getCriterion().getMaxScore())
                .weight(saved.getWeight()) // Trọng số mới vừa cập nhật đây rồi
                .build();

        // Chuẩn bị gói phản hồi báo sửa thành công
        ApiResponse<RoundCriteriaResponse> response = ApiResponse.<RoundCriteriaResponse>builder()
                .success(true)
                .message("Cập nhật trọng số tiêu chí thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        // Trả về mã 200 OK
        return ResponseEntity.ok(response);
    }

    /**
     * API 5: Xóa một tiêu chí ra khỏi đợt đánh giá.
     */
    @DeleteMapping("/{id}") // Chờ nhận yêu cầu xóa (HTTP DELETE)
    @PreAuthorize("hasRole('ADMIN')") // Quyền hạn: Duy nhất Admin mới được xóa
    public ResponseEntity<ApiResponse<Object>> deleteRoundCriteria(@PathVariable Integer id) { // Lấy ID cần xóa
        
        // Kiểm tra xem ID này có trong CSDL không, không có thì báo lỗi luôn
        RoundCriteria rc = roundCriteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí đợt đánh giá với ID: " + id));

        // Tiến hành xóa sạch bản ghi này khỏi CSDL
        roundCriteriaRepository.delete(rc);

        // Tạo thông báo xóa thành công (không cần trả về data vì đã xóa mất rồi)
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Xóa tiêu chí khỏi đợt đánh giá thành công")
                .timestamp(LocalDateTime.now())
                .build();

        // Trả về mã 200 OK báo xóa thành công
        return ResponseEntity.ok(response);
    }
}


