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
public class PromptTemplateController {

    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;
    private final ChatClient gemmaChatClient;

    public PromptTemplateController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                          @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
                          @Qualifier("gemmaChatClient") ChatClient gemmaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
        this.gemmaChatClient = gemmaChatClient;
    }

    @Value("classpath:/promptTemplate/userPromptTemplate.st")
    Resource userPromptTemplate;

    @GetMapping(value = "template/gemma/email")
    ResponseEntity<String> templateGemmaEmailResponse(@RequestParam("customerName") String customerName,
                                              @RequestParam("customerMessage") String customerMessage){
        String result = gemmaChatClient.prompt()
                .system("""
                        Act as a professional customer support assistant. Write a polite, empathetic, and concise email response based on the provided customer query and key resolution points, using clear placeholders for names. Output only the ready-to-send draft without any conversational intro or outro text.
                        """)
                .user(template -> template.text(userPromptTemplate)
                        .param("customerName", customerName)
                        .param("customerMessage", customerMessage))
                .call().content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "template/openai/chat")
    ResponseEntity<String> templateOpenAIChat(@RequestParam("message") String message){
        String result = openAiChatClient.prompt(message).call().content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "template/ollama/chat")
    ResponseEntity<String> templateOllamaChat(@RequestParam("message") String message){
        String result = ollamaChatClient.prompt(message).call().content();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
