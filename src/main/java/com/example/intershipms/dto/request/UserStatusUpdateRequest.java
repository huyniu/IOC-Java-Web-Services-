package com.example.intershipms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatusUpdateRequest {

    @NotNull(message = "Trạng thái kích hoạt (isActive) không được để trống")
    private Boolean isActive;
}
