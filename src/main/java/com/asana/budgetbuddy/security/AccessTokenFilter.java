package com.asana.budgetbuddy.security;

import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.service.UserService;
import com.asana.budgetbuddy.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Log4j2
public class AccessTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Optional<String> accessToken = parseAccessToken(request);
            if(accessToken.isPresent() && jwtUtil.validateAccessToken(accessToken.get())) {
                String userId = jwtUtil.getUserIdFromAccessToken(accessToken.get());
                Optional<User> user = userService.getById(Long.parseLong(userId));
                UsernamePasswordAuthenticationToken passwordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                user.get(),
                                null,
                                user.get().getAuthorities()
                        );
                passwordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
            }
        } catch (Exception e) {
            log.error("Cannot authenticate: ", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Parse the Access Token from the request header and removes the Bearer from it
     * @param request
     * @return String (Access Token)
     */
    private Optional<String> parseAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.replace("Bearer ", ""));
        }
        return Optional.empty();
    }
}