package com.example.intershipms.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    // Tất cả field đều là tùy chọn (optional) để hỗ trợ partial update.
    // Client chỉ cần gửi những field muốn thay đổi; field không gửi sẽ là null
    // và logic trong Service sẽ bỏ qua, giữ nguyên giá trị cũ trong DB.

    private String fullName;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String phoneNumber;
}
