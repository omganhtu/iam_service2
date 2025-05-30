// File: src/main/java/com/example/iam_service2/config/RequestResponseLoggingFilter.java
package com.example.iam_service2.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final String[] SENSITIVE_FIELDS = {"password", "newPassword"};

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        String body = IOUtils.toString(wrappedRequest.getInputStream(), StandardCharsets.UTF_8);

        for (String sensitive : SENSITIVE_FIELDS) {
            body = body.replaceAll("(?i)\"" + sensitive + "\"\\s*:\\s*\".*?\"", "\"" + sensitive + "\":\"***\"");
        }

        log.info("➡️ [{}] {}{} - body: {}", method, uri, (query != null ? "?" + query : ""), body);

        filterChain.doFilter(wrappedRequest, response);

        log.info("⬅️ [{}] {} - status: {}", method, uri, response.getStatus());
    }
}
