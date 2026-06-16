package com.ai.config;

import com.ai.advisors.TokenUsageAuditAdvisor;
import com.ai.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class HelpDeskChatClientConfig {

    @Value("classpath:/promptTemplate/systemPromptDocDataTemplate.st")
    Resource systemPromptDocDataTemplate;

    @Bean("openAiHelpdeskChatClient")
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPromptDocDataTemplate)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(),tokenUsageAdvisor,memoryAdvisor));
        return chatClientBuilder.build();
    }

    /*public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel, @Qualifier("chatMemoryToolCalling") ChatMemory chatMemory, TimeTools timeTools) {
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPromptDocDataTemplate)
                .defaultTools(timeTools)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(),tokenUsageAdvisor,memoryAdvisor));
        return chatClientBuilder.build();
    }*/
}
