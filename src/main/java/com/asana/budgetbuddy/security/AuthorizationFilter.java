package com.asana.budgetbuddy.security;

import com.asana.budgetbuddy.util.JwtUtil;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * AuthorizationFilter will be invoked on every request.
 * This class verify the Authorization token inside the header request.
 */
@Slf4j
@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * The filter will trigger for each request that
     * doesn't belong to the /auth endpoints.
     */
    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        // all requests that isn't to auth path will be verified
        if (!request.getRequestURI().contains("/auth")) {
            // the verification is the Authorization property in header
            if (request.getHeader("authorization") == null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter out = response.getWriter();
                out.print(invalidTokenBuilder(request));
                return;
            }
            String token = request.getHeader("authorization");
            String access_token = token.replace("Basic ", "");
            try {
                // verify the access token validation using jwt decrypt
                boolean isValidToken = jwtUtil.validateAccessToken(access_token);
                if (!isValidToken) {
                    // if token is invalid, creates a JSON with java's standard error response
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    PrintWriter out = response.getWriter();
                    out.print(invalidTokenBuilder(request));
                    return;
                }
            } catch (JWTVerificationException e) {
                log.error(e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Builds a JSON return for invalid token responses.
     * @param request HttpServletRequest
     * @return jsonObject JSON object made
     */
    public JSONObject invalidTokenBuilder(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", LocalDateTime.now());
        jsonObject.put("status", 401);
        jsonObject.put("error", "Unauthorized");
        jsonObject.put("message", "Invalid access token");
        jsonObject.put("path", request.getRequestURI());
        return jsonObject;
    }
}
