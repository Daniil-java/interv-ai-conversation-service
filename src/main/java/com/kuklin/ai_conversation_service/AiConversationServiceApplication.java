package com.kuklin.ai_conversation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AiConversationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiConversationServiceApplication.class, args);
	}

}
