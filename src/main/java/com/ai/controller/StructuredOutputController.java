package com.ai.controller;

import com.ai.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class StructuredOutputController {
    private final ChatClient openAiChatClient;
    private final ChatClient gemmaChatClient;

    public StructuredOutputController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                            @Qualifier("gemmaChatClient") ChatClient gemmaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.gemmaChatClient = gemmaChatClient;
    }

    @GetMapping(value = "bean/gemma/chat")
    ResponseEntity<CountryCities> gemmaChat(@RequestParam("message") String message){
        CountryCities  result = gemmaChatClient
                .prompt()
                .user(message)
                .call()
                .entity(CountryCities.class);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "bean/gemma/chat/list")
    ResponseEntity<List<String>> gemmaChatList(@RequestParam("message") String message){
        List<String>  result = gemmaChatClient
                .prompt()
                .user(message)
                .call()
                .entity(new ListOutputConverter());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "bean/gemma/chat/map")
    ResponseEntity<Map<String,Object>> gemmaChatMap(@RequestParam("message") String message){
        Map<String, Object>  result = gemmaChatClient
                .prompt()
                .user(message)
                .call()
                .entity(new MapOutputConverter());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "bean/gemma/chat/map/pojo")
    ResponseEntity<List<CountryCities>> gemmaChatMapPojo(@RequestParam("message") String message){
        List<CountryCities>  result = gemmaChatClient
                .prompt()
                .user(message)
                .call()
                .entity(new ParameterizedTypeReference<List<CountryCities>>() {});
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "bean/openai/chat")
    ResponseEntity<CountryCities> openAIChat(@RequestParam("message") String message){
        CountryCities result = openAiChatClient.prompt(message).call().entity(CountryCities.class);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
