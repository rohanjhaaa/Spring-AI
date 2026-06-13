package com.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final ChatClient gemmaChatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/promptTemplate/systemPromptRandomDataTemplate.st")
    Resource promptTemplate;

    @Value("classpath:/promptTemplate/systemPromptDocDataTemplate.st")
    Resource promptHRTemplate;

    public RagController(@Qualifier("gemmaChatMemoryClient") ChatClient gemmaChatClient, VectorStore vectorStore) {
        this.gemmaChatClient = gemmaChatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping(value = "random/gemma/chat")
    ResponseEntity<String> gemmaChat(@RequestParam("message") String message, @RequestHeader("username") String userName){
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)
                .build();
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocuments.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));

        String  result = gemmaChatClient
                .prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(promptTemplate)
                        .param("documents",similarContext))
                .user(message).advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID,userName))
                .call()
                .content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "doc/gemma/chat")
    ResponseEntity<String> gemmaDocumentChat(@RequestParam("message") String message, @RequestHeader("username") String userName){
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)
                .build();
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocuments.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));

        String  result = gemmaChatClient
                .prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(promptHRTemplate)
                        .param("documents",similarContext))
                .user(message).advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID,userName))
                .call()
                .content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // its already defined in bean
    @GetMapping(value = "doc/gemma/chat/v2")
    ResponseEntity<String> gemmaDocumentChatWithoutSystemPrompt(@RequestParam("message") String message, @RequestHeader("username") String userName){
        String  result = gemmaChatClient
                .prompt()
                .user(message).advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID,userName))
                .call()
                .content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
