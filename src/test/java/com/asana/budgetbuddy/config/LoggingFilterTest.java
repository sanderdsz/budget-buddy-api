package com.asana.budgetbuddy.config;

import com.asana.budgetbuddy.shared.util.LoggingFilter;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

@SpringBootTest
public class LoggingFilterTest {

    @Test
    void testDoFilter() throws ServletException, IOException {
        LoggingFilter filter = new LoggingFilter();
        MockFilterChain mockFilterChain = new MockFilterChain();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        filter.doFilter(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
    }
}
