package com.ai.openAI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DockerModelApplication {

	public static void main(String[] args) {
		SpringApplication.run(DockerModelApplication.class, args);
		System.out.println("====================::- SPRING AI : Docker Model (Gemma-3) AI Started.... -::====================");
	}

}
