package com.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class StreamController {

    private final ChatClient openAiChatClient;
    private final ChatClient gemmaChatClient;

    public StreamController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                          @Qualifier("gemmaChatClient") ChatClient gemmaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.gemmaChatClient = gemmaChatClient;
    }

    @GetMapping(value = "stream/gemma/chat")
    ResponseEntity<Flux<String>> gemmaChat(@RequestParam("message") String message){
        Flux<String> result = gemmaChatClient
                .prompt()
                .user(message)
                .stream()
                .content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "stream/openai/chat")
    ResponseEntity<String> openAIChat(@RequestParam("message") String message){
        String result = openAiChatClient.prompt(message).call().content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
