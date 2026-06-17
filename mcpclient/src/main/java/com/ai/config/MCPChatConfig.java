package com.ai.config;

import com.ai.advisors.TokenUsageAuditAdvisor;
import com.openai.client.OpenAIClient;
import com.openai.client.OpenAIClientImpl;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MCPChatConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean("mcpOpenAIChatModel")
    public OpenAiChatModel openAiChatModel(ToolCallbackProvider toolCallbackProvider) {
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .apiKey(apiKey)
                .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
        return OpenAiChatModel.builder()
                .options(openAiChatOptions)
                .build();
    }

    @Bean("mcpOpenAIChatClient")
    public ChatClient openAiChatClient(@Qualifier("mcpOpenAIChatModel")OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor, new TokenUsageAuditAdvisor()));
        return chatClientBuilder.build();
    }

}
