package com.example.intershipms.service.impl;

import com.example.intershipms.dto.request.UserCreationRequest;
import com.example.intershipms.dto.request.UserRoleUpdateRequest;
import com.example.intershipms.dto.request.UserStatusUpdateRequest;
import com.example.intershipms.dto.request.UserUpdateRequest;
import com.example.intershipms.dto.response.UserResponse;
import com.example.intershipms.entity.User;
import com.example.intershipms.exception.BadRequestException;
import com.example.intershipms.exception.ResourceNotFoundException;
import com.example.intershipms.repository.UserRepository;
import com.example.intershipms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với username: " + username));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Integer id, UserUpdateRequest request) {
        // Bước 1: Lấy đối tượng User hiện tại từ DB.
        // Nếu không tìm thấy, ném 404 ngay lập tức.
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        // Bước 2: Partial Update — chỉ cập nhật field nào Client gửi lên (khác null và khác rỗng).
        // Các field không được gửi sẽ giữ nguyên giá trị cũ đang có trong DB.

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            existingUser.setFullName(request.getFullName());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            existingUser.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            existingUser.setPhoneNumber(request.getPhoneNumber());
        }

        // Bước 3: Lưu đối tượng đã được cập nhật một phần vào DB.
        User updatedUser = userRepository.save(existingUser);
        return mapToResponse(updatedUser);
    }

    @Override
    public UserResponse updateUserStatus(Integer id, UserStatusUpdateRequest request) {
        // 1. Lấy username của người đang thực hiện request từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // 2. Tìm user mục tiêu, ném 404 nếu không tồn tại
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        // 3. Ngăn Admin tự thay đổi trạng thái tài khoản của chính mình
        if (currentUsername.equals(user.getUsername())) {
            throw new BadRequestException("Lỗi: Bạn không thể tự thay đổi trạng thái tài khoản của chính mình!");
        }

        user.setIsActive(request.getIsActive());
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public UserResponse updateUserRole(Integer id, UserRoleUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        if (user.getRole() == User.Role.ADMIN && request.getRole() != User.Role.ADMIN) {
            throw new BadRequestException("Không thể giáng cấp tài khoản Admin!");
        }

        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Integer id) {
        // 1. Lấy thông tin người dùng đang đăng nhập từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // 2. Tìm user cần xóa, ném 404 nếu không tồn tại
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        // 3. So sánh username của người đang đăng nhập với user bị xóa
        if (currentUsername.equals(user.getUsername())) {
            throw new BadRequestException("Bạn không thể tự xóa tài khoản của chính mình!");
        }

        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }
}