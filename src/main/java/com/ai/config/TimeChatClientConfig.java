package com.ai.config;

import com.ai.advisors.TokenUsageAuditAdvisor;
import com.ai.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class TimeChatClientConfig {

    @Bean("openAiTimeChatClient")
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel,ChatMemory chatMemory, TimeTools timeTools) {
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(openAiChatModel)
                .defaultTools(timeTools)
                //.defaultAdvisors(List.of(new SimpleLoggerAdvisor(),tokenUsageAdvisor));
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(),tokenUsageAdvisor,memoryAdvisor));
        return chatClientBuilder.build();
    }
}
