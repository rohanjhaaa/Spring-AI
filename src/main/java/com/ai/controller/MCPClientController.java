package com.ai.controller;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/mcp")
public class MCPClientController {

    private final ChatClient openAIChatClient;

    public MCPClientController(@Qualifier("mcpOpenAIChatClient") ChatClient openAIChatClient) {
        this.openAIChatClient = openAIChatClient;
    }

    @GetMapping(value = "open-ai/chat")
    ResponseEntity<String> openAIChat(@RequestParam("message") String message, @RequestHeader("username") String userName){
        String  result = openAIChatClient
                .prompt()
                .user(message).advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID,userName))
                .call()
                .content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
