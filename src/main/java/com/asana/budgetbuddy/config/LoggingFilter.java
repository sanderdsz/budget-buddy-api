package com.asana.budgetbuddy.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Arrays;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        try {
            chain.doFilter(wrappedRequest, res);
        } finally {
            logRequestBody(wrappedRequest);
        }
    }

    private static void logRequestBody(ContentCachingRequestWrapper request) {

        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                String requestBody = new String(buf, 0, buf.length, request.getCharacterEncoding());
                logger.info("body=[" + requestBody + "]");
            } catch (Exception e) {
                System.out.println("error in reading request body");
            }
        }
    }
}
