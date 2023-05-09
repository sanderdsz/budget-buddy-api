package com.asana.budgetbuddy.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    private static void requestLoggerBuilder(ContentCachingRequestWrapper request) {
        JSONObject body = stringToJsonBody(request);
        JSONObject headers = stringToJsonHeaders(request);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "request");
        jsonObject.put("body", body);
        jsonObject.put("headers", headers);
        logger.info(jsonObject.toString(4));
    }

    private static JSONObject stringToJsonHeaders(ContentCachingRequestWrapper request) {
        JSONObject headers = new JSONObject();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    private static JSONObject stringToJsonBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                String requestBody = new String(buf, 0, buf.length, request.getCharacterEncoding());
                return new JSONObject(requestBody);
            } catch (Exception e) {
                System.out.println("error in reading request body");
            }
        }
        return null;
    }

    public void doFilter(
            ServletRequest req,
            ServletResponse res,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        try {
            chain.doFilter(wrappedRequest, response);
        } finally {
            requestLoggerBuilder(wrappedRequest);
        }
    }
}
