package com.asana.budgetbuddy.security;

import com.asana.budgetbuddy.util.JwtUtil;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthorizationFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!req.getRequestURI().contains("/auth")) {
            String token = req.getHeader("authorization");
            String access_token = token.replace("Basic ", "");
            log.info("TOKEN: " + token);
            try {
                Boolean isValidToken = jwtUtil.validateAccessToken(access_token);
                log.info("TOKEN: " + isValidToken);
            } catch (JWTVerificationException e) {
                log.error(e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}
