package com.ai.config;

import com.ai.advisors.TokenUsageAuditAdvisor;
import com.ai.rag.ScoreFilterPostProcessor;
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
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Vector;

@Configuration
public class ChatMemoryConfig {

    @Value("classpath:/promptTemplate/systemPromptDocDataTemplate.st")
    Resource promptHRTemplate;

    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();
    }

//    @Bean("chatMemoryToolCalling")
//    ChatMemory chatMemoryToolCalling(){
//        return MessageWindowChatMemory.builder().build();
//    }

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
                .options(
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
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(gemmaModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor, tokenUsageAdvisor));
        return chatClientBuilder.build();
    }

    @Bean("gemmaRagChatMemoryClient")
    public ChatClient gemmaRagChatClient(@Qualifier("memoryGemmaModel") OllamaChatModel gemmaModel, ChatMemory chatMemory, RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(gemmaModel)
                .defaultSystem(promptHRTemplate)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor,tokenUsageAdvisor,retrievalAugmentationAdvisor));
        return chatClientBuilder.build();
    }

    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore, @Qualifier("memoryGemmaModel")
    OllamaChatModel gemmaModel, ScoreFilterPostProcessor scoreFilterPostProcessor){
        ChatClient.Builder builder = ChatClient.builder(gemmaModel);
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(TranslationQueryTransformer.builder().chatClientBuilder(builder)
                        .targetLanguage("english").build())
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build())
                .documentPostProcessors(scoreFilterPostProcessor)  // used to post processor document
                .build();
    }

//    @Bean
//    public ChatClient ollamaChatClient(@Qualifier("llamaModel")OllamaChatModel ollamaChatModel){
//        ChatClient.Builder chatClientBuilder = ChatClient.builder(ollamaChatModel)
//                .defaultAdvisors(new SimpleLoggerAdvisor());
//        return chatClientBuilder.build();
//    }

}
