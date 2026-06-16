package com.ai.config;

import com.ai.advisors.TokenUsageAuditAdvisor;
import com.ai.rag.WebSearchDocumentRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class WebSearchRAGChatClientConfig {

    @Bean("WebSearchRAGChatClient")
    public ChatClient chatClient( @Qualifier("memoryGemmaModel") OllamaChatModel model,
                                 ChatMemory chatMemory, RestClient.Builder restClientBuilder,
                                  @Value("${tavily.base-url}") String baseUrl,
                                  @Value("${tavily.api-key}") String apiKey){
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor tokenAdvisor = new TokenUsageAuditAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        var webSearchRAGAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(WebSearchDocumentRetriever.builder()
                        .baseUrl(baseUrl)
                        .apiKey(apiKey)
                        .restClientBuilder(restClientBuilder)
                        .maxResults(5).build()).build();
        return ChatClient.builder(model).defaultAdvisors(List.of(loggerAdvisor,tokenAdvisor,memoryAdvisor,webSearchRAGAdvisor)).build();
    }

}
