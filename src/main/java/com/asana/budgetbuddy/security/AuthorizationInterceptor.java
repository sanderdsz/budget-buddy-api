package com.asana.budgetbuddy.security;

import com.asana.budgetbuddy.util.JwtUtil;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        logger.info("[preHandle]");
        String token = request.getHeader("authorization");
        String access_token = token.replace("Basic ", "");
        try {
            Boolean isValidToken = jwtUtil.validateAccessToken(access_token);
            return isValidToken;
        } catch (JWTVerificationException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) throws Exception {
        logger.info("[postHandle]");
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) throws Exception {
        logger.info("[afterCompletion]");
    }
}
