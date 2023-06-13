package com.asana.budgetbuddy;

import com.asana.budgetbuddy.security.AuthorizationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BudgetBuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetBuddyApplication.class, args);
	}
}
