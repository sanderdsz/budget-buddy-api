package com.asana.budgetbuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BudgetBuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetBuddyApplication.class, args);
	}

}
