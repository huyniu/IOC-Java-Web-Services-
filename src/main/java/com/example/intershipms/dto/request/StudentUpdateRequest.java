package com.example.intershipms.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentUpdateRequest {

    // Tất cả field đều là tùy chọn (optional) để hỗ trợ partial update.
    // Client chỉ cần gửi những field muốn thay đổi; field không gửi sẽ là null
    // và logic trong Service sẽ bỏ qua, giữ nguyên giá trị cũ trong DB.

    private String major;
    private String className;
    private LocalDate dateOfBirth;
    private String address;
}