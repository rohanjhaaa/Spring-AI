package com.ai;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class MultiModelApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiModelApplication.class, args);
		System.out.println("====================::- SPRING AI : Multi-Model Started.... -::====================");
	}

}
