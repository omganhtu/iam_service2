package com.example.iam_service2.security;

import com.example.iam_service2.service.UserDetailsServiceImpl;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

public class JwtAuthenticationConverterWithIamValidation implements Converter<Jwt, JwtAuthenticationToken> {

    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationConverterWithIamValidation(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại trong IAM DB: " + username);
        }

        return new JwtAuthenticationToken(jwt, userDetails.getAuthorities(), username);
    }
}
