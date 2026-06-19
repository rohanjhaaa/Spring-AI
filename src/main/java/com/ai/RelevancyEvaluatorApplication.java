package com.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

@SpringBootApplication
@EnableRetry
public class RelevancyEvaluatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RelevancyEvaluatorApplication.class, args);
		System.out.println("====================::- SPRING AI : Multi-Model Started.... -::====================");
	}

}
