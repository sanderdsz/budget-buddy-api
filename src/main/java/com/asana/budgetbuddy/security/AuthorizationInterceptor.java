package com.asana.budgetbuddy.security;

import com.asana.budgetbuddy.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Enumeration;


@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        if (request.getRequestURI().contains("/auth")) {
            logger.info(requestStringToJsonHeaders(request).toString());
        } else {
            logger.info(requestStringToJsonHeaders(request).toString());
            JSONObject headers = requestStringToJsonHeaders(request);
            String token = headers.get("authorization")
                    .toString()
                    .replace("Basic ", "");
            logger.info(token);
            Boolean isValidToken = jwtUtil.validateRefreshToken(token);
            logger.info(isValidToken.toString());
        }
        logger.info("[preHandle][" + request + "]"
                + "[" + request.getMethod()
                + "]" + request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) throws Exception {
        logger.info("[postHandle][" + request + "]");
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) throws Exception {
        logger.info("[afterCompletion][" + request + "][exception: " + ex + "]");
    }

    private static JSONObject requestStringToJsonHeaders(HttpServletRequest request) {
        JSONObject headers = new JSONObject();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    private static JSONObject responseStringToJsonHeaders(HttpServletResponse response) {
        JSONObject headers = new JSONObject();
        Collection<String> headerNames = response.getHeaderNames();
        headerNames.forEach(headerName ->
                response.getHeaders(headerName).forEach(headerValue ->
                        headers.put(headerName, headerValue)
                )
        );
        return headers;
    }

}
