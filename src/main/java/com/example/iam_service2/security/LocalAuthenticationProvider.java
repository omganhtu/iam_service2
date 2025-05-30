package com.example.iam_service2.security;

import com.example.iam_service2.entity.User;
import com.example.iam_service2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class LocalAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Lấy user từ DB nội bộ
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Kiểm tra mật khẩu (nên dùng PasswordEncoder nếu hash)
        if (!password.equals(user.getPassword())) {
            throw new BadCredentialsException("Sai mật khẩu");
        }

        // Nếu đúng, trả về authenticated token
        return new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, Collections.emptyList() // Có thể map role sang authority nếu muốn
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
