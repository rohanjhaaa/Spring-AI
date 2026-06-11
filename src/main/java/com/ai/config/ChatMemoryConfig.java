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
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatMemoryConfig {

    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();
    }

//    @Bean
//    public OllamaChatModel llamaModel(OllamaApi ollamaApi) {
//
//        return OllamaChatModel.builder()
//                .ollamaApi(ollamaApi)
//                .defaultOptions(
//                        OllamaChatOptions.builder()
//                                .model("llama3.2:1b")
//                                .build())
//                .build();
//    }

    @Bean("memoryGemmaModel")
    public OllamaChatModel gemmaModel(OllamaApi ollamaApi) {

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaChatOptions.builder()
                                .model("gemma3:latest")
                                .build())
                .build();
    }

//    @Bean
//    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
////        var chatOptions = OpenAiChatOptions.builder().model("gpt-5.4-mini").temperature(0.8);
//        ChatClient.Builder chatClientBuilder = ChatClient.builder(openAiChatModel)
//                //.defaultOptions(chatOptions.build())
//                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor()));
//        return chatClientBuilder.build();
//    }

    @Bean("gemmaChatMemoryClient")
    public ChatClient gemmaChatClient(@Qualifier("memoryGemmaModel") OllamaChatModel gemmaModel, ChatMemory chatMemory) {
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(gemmaModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor));
        return chatClientBuilder.build();
    }

//    @Bean
//    public ChatClient ollamaChatClient(@Qualifier("llamaModel")OllamaChatModel ollamaChatModel){
//        ChatClient.Builder chatClientBuilder = ChatClient.builder(ollamaChatModel)
//                .defaultAdvisors(new SimpleLoggerAdvisor());
//        return chatClientBuilder.build();
//    }

}
