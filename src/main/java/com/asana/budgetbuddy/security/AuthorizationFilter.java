package com.asana.budgetbuddy.security;

import com.asana.budgetbuddy.util.JwtUtil;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * AuthorizationFilter will be invoked on every request.
 * This class verify the Authorization token inside the header request.
 */
@Slf4j
@Component
public class AuthorizationFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * The filter will trigger for each request that
     * doesn't belong to the /auth endpoints.
     */
    @Override
    public void doFilter(
            ServletRequest req,
            ServletResponse res,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // all requests that isn't to auth path will be verified
        if (!request.getRequestURI().contains("/auth")) {
            // the verification is the Authorization property in header
            if (request.getHeader("Authorization") == null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter out = response.getWriter();
                out.print(invalidTokenBuilder(request));
                return;
            }
            String token = request.getHeader("Authorization");
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
     *
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

    /**
     * Method to enable CORS not only in controllers
     * but also filters and interceptors.
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        //config.setAllowCredentials(true); // you USUALLY want this
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
