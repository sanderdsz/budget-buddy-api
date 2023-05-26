package com.asana.budgetbuddy.config;

import com.asana.budgetbuddy.security.AuthorizationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

    //@Bean
    public FilterRegistrationBean<AuthorizationFilter> authFilter() {
        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthorizationFilter());
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
