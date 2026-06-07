package com.ai.openAI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BedrockApplication {

	public static void main(String[] args) {
		SpringApplication.run(BedrockApplication.class, args);
		System.out.println("====================::- SPRING AI : Bedrock service Started.... -::====================");
	}

	@Component
	public class StartupCheck implements CommandLineRunner {

		@Value("${spring.ai.bedrock.converse.chat.options.model:NOT_FOUND}")
		private String model;

		@Override
		public void run(String... args) {
			System.out.println("MODEL => " + model);
		}
	}

}
