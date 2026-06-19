package com.ai.controller;

import com.ai.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api")
public class ChatMemoryController {

    private final ChatClient gemmaChatClient;

    public ChatMemoryController(@Qualifier("gemmaChatMemoryClient") ChatClient gemmaChatClient) {
        this.gemmaChatClient = gemmaChatClient;
    }

    @GetMapping(value = "memory/gemma/chat")
    ResponseEntity<String> gemmaChat(@RequestParam("message") String message, @RequestHeader("username") String userName){
        String  result = gemmaChatClient
                .prompt()
                .user(message).advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID,userName))
                .call()
                .content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
