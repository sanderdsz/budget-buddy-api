package com.asana.budgetbuddy.security;

import com.asana.budgetbuddy.util.JwtUtil;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Date;

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
            try {
                boolean isValidToken = jwtUtil.validateAccessToken(access_token);
                if (!isValidToken) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("timestamp", LocalDateTime.now());
                    jsonObject.put("status", 401);
                    jsonObject.put("error", "Unauthorized");
                    jsonObject.put("message", "Invalid access token");
                    jsonObject.put("path", req.getRequestURI());
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    PrintWriter out = response.getWriter();
                    out.print(jsonObject);
                    return;
                }
            } catch (JWTVerificationException e) {
                log.error(e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
