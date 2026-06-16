package com.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/tools")
public class TimeController {

    private final ChatClient openAiChatClient;

    public TimeController(@Qualifier("openAiTimeChatClient") ChatClient openAiChatClient) {
        this.openAiChatClient = openAiChatClient;
    }
    // Gemma3 does not support tools
    @GetMapping(value = "/local-time")
    ResponseEntity<String> openAIChat(@RequestParam("message") String message, @RequestHeader("username") String userName){
        String  result = openAiChatClient
                .prompt()
                .user(message).advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID,userName))
                .call()
                .content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
