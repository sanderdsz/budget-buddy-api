package com.asana.budgetbuddy.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.*;

/*
 * This is a custom filter for logging responses and requests.
 */
//@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    private long startTime = System.nanoTime();

    private static void requestLoggerBuilder(ContentCachingRequestWrapper request) {
        JSONObject body = requestStringToJsonBody(request);
        JSONObject headers = requestStringToJsonHeaders(request);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "request");
        jsonObject.put("method", request.getMethod());
        jsonObject.put("uri", request.getRequestURL());
        jsonObject.put("path", request.getServletPath());
        jsonObject.put("host", request.getRemoteHost());
        jsonObject.put("body", body);
        jsonObject.put("headers", headers);
        logger.info(jsonObject.toString(4));
    }

    private static JSONObject requestStringToJsonHeaders(ContentCachingRequestWrapper request) {
        JSONObject headers = new JSONObject();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    private static JSONObject responseStringToJsonHeaders(ContentCachingResponseWrapper response) {
        JSONObject headers = new JSONObject();
        Collection<String> headerNames = response.getHeaderNames();
        headerNames.forEach(headerName ->
                response.getHeaders(headerName).forEach(headerValue ->
                        headers.put(headerName, headerValue)
                )
        );
        return headers;
    }

    private static JSONObject requestStringToJsonBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                String requestBody = new String(buf, 0, buf.length, request.getCharacterEncoding());
                return new JSONObject(requestBody);
            } catch (Exception e) {
                logger.error("error in reading request body");
            }
        }
        return null;
    }

    private static JSONObject responseStringToJsonBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                String requestBody = new String(buf, 0, buf.length, response.getCharacterEncoding());
                return new JSONObject(requestBody);
            } catch (Exception e) {
                logger.error("error in reading response body");
            }
        }
        return null;
    }

    private void responseLoggerBuilder(ContentCachingResponseWrapper response) {
        JSONObject body = responseStringToJsonBody(response);
        JSONObject headers = responseStringToJsonHeaders(response);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "response");
        jsonObject.put("status", response.getStatus());
        jsonObject.put("body", body);
        jsonObject.put("headers", headers);
        jsonObject.put("duration: ", ((System.nanoTime() - this.startTime) / 1000000 + " ms"));
        logger.info(jsonObject.toString(4));
    }

    public void doFilter(
            ServletRequest req,
            ServletResponse res,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        try {
            this.startTime = System.nanoTime();
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            requestLoggerBuilder(wrappedRequest);
            responseLoggerBuilder(wrappedResponse);
        }
    }
}
