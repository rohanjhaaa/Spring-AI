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
public class ChatController {

    @Value("classpath:/promptTemplates/hrPolicy.st")
    Resource hrPolicyTemplate;

    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;
    private final ChatClient gemmaChatClient;

    public ChatController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                          @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
                          @Qualifier("gemmaChatClient") ChatClient gemmaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
        this.gemmaChatClient = gemmaChatClient;
    }

    @GetMapping(value = "gemma/chat")
    public ResponseEntity<String> gemmaChat(@RequestParam("message") String message){
        String result = gemmaChatClient.prompt(message).call().content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("gemma/prompt-stuffing")
    public String promptStuffing(@RequestParam("message") String message) {
        return gemmaChatClient
                .prompt().system(hrPolicyTemplate)
                .user(message)
                .call().content();
    }

    @GetMapping(value = "openai/chat")
    public ResponseEntity<String> openAIChat(@RequestParam("message") String message){
        String result = openAiChatClient.prompt(message).call().content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "ollama/chat")
    public ResponseEntity<String> ollamaChat(@RequestParam("message") String message){
        String result = ollamaChatClient.prompt(message).call().content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
