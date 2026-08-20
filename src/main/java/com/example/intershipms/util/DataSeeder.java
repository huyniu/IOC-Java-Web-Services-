package com.example.intershipms.util;

import com.example.intershipms.entity.User;
import com.example.intershipms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem hệ thống đã có tài khoản admin nào chưa
        if (!userRepository.existsByUsername("admin")) {
            User defaultAdmin = User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("123456"))
                    .fullName("Dinh Quoc Huy")
                    .email("dhuy271105@gmail.com")
                    .role(User.Role.ADMIN)
                    .isActive(true)
                    .build();

            userRepository.save(defaultAdmin);
            log.info("Đã khởi tạo tài khoản ADMIN mặc định thành công!");
            log.info("Username: admin | Password: 123456");
        }
    }
}