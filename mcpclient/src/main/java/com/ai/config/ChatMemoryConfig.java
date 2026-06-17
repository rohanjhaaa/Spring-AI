package com.ai.config;

import com.ai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import java.util.List;

@Configuration
public class ChatMemoryConfig {

    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();
    }

    @Bean("memoryGemmaModel")
    public OllamaChatModel gemmaModel(OllamaApi ollamaApi) {

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .options(
                        OllamaChatOptions.builder()
                                .model("gemma3:latest")
                                .build())
                .build();
    }

    @Bean("gemmaChatMemoryClient")
    public ChatClient gemmaChatClient(@Qualifier("memoryGemmaModel") OllamaChatModel gemmaModel, ChatMemory chatMemory) {
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(gemmaModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor, tokenUsageAdvisor));
        return chatClientBuilder.build();
    }

}
