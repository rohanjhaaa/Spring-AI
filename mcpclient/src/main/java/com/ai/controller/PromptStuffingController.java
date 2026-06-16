package com.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PromptStuffingController {

    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;
    private final ChatClient gemmaChatClient;

    public PromptStuffingController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                                    @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
                                    @Qualifier("gemmaChatClient") ChatClient gemmaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
        this.gemmaChatClient = gemmaChatClient;
    }

    @Value("classpath:/promptTemplate/systemPromptTemplate.st")
    Resource systemPromptTemplate;
    @Value("classpath:/promptTemplate/hrPromptTemplate.st")
    Resource hrPromptTemplate;

    @GetMapping(value = "template/gemma/stuffing")
    ResponseEntity<String> promptStuffing(@RequestParam("message") String message){
        String result = gemmaChatClient.prompt()
                .system(systemPromptTemplate)
                .user(hrPromptTemplate)
                .user(message)
                .call().content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
