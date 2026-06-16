package com.ai.controller;

import com.ai.tools.HelpdeskTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/tools")
public class HelpdeskController {

    @Qualifier("openAiTimeChatClient")
    private final ChatClient openAiChatClient;
    private final HelpdeskTools helpdeskTools;

    public HelpdeskController(@Qualifier("openAiTimeChatClient") ChatClient openAiChatClient, HelpdeskTools helpdeskTools) {
        this.openAiChatClient = openAiChatClient;
        this.helpdeskTools = helpdeskTools;
    }

    @GetMapping(value = "/help-desk")
    ResponseEntity<String> openAIChat(@RequestParam("message") String message, @RequestHeader("username") String userName){
        String  result = openAiChatClient
                .prompt()
                .user(message).advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID,userName))
                .tools(helpdeskTools)
                .toolContext(Map.of("username",userName))
                .call()
                .content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
