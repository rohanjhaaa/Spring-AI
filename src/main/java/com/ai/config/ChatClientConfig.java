package com.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public OllamaChatModel llamaModel(OllamaApi ollamaApi) {

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaChatOptions.builder()
                                .model("llama3.2:1b")
                                .build())
                .build();
    }

    @Bean
    public OllamaChatModel gemmaModel(OllamaApi ollamaApi) {

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaChatOptions.builder()
                                .model("gemma3:latest")
                                .build())
                .build();
    }

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(openAiChatModel);
        return chatClientBuilder.build();
    }

    @Bean
    public ChatClient gemmaChatClient(@Qualifier("gemmaModel") OllamaChatModel gemmaModel) {
        return ChatClient.builder(gemmaModel)
                .defaultSystem("""
                You are the internal HR Assistant for R.J Group. Your sole role is to help employees navigate and understand company HR policies, benefits, and procedures.              
                STRICT CONSTRAINT: You must only handle queries directly related to company HR policies. If an employee asks an unrelated question (e.g., general knowledge, technical tasks, or casual chat), you must kindly decline to answer and politely ask them to stick to HR policy-related questions.                                
                Guidelines for your responses:
                1. Accuracy First: Base your answers strictly on the provided company handbook and documentation. If an answer cannot be found, politely direct the employee to contact the HR team at [HR Email].
                2. Tone: Be helpful, welcoming, and clear.\s
                3. Confidentiality: Handle all queries with strict confidentiality.
                Greet the employee and kindly ask how you can help them with their HR policy queries today.
                """).build();
    }

    @Bean
    public ChatClient ollamaChatClient(@Qualifier("llamaModel")OllamaChatModel ollamaChatModel){
        ChatClient.Builder chatClientBuilder = ChatClient.builder(ollamaChatModel);
        return chatClientBuilder.build();
    }

}
