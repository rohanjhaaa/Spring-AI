package com.ai.controller;

import com.openai.models.audio.AudioResponseFormat;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api")
public class AudioController {

    private final ChatClient gemmaChatClient;
    private final TranscriptionModel transcriptionModel;

    public AudioController(@Qualifier("gemmaChatMemoryClient") ChatClient gemmaChatClient, TranscriptionModel transcriptionModel) {
        System.out.println("Injected chat client = " +gemmaChatClient.getClass().getName());
        this.gemmaChatClient = gemmaChatClient;
        this.transcriptionModel = transcriptionModel;
    }

    @GetMapping(value = "/transcribe")
    ResponseEntity<String> gemmaChat(@Value("classpath:TestAudio.mp3")Resource audioFile){
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        return new ResponseEntity<>(response.getResult().getOutput(), HttpStatus.OK);
    }

    @GetMapping(value = "/transcribe-options")
    ResponseEntity<String> transcribeWithOptions(@Value("classpath:TestAudio.mp3")Resource audioFile){
        AudioTranscriptionResponse response = transcriptionModel.call(
                new AudioTranscriptionPrompt(audioFile, OpenAiAudioTranscriptionOptions.builder()
                        .prompt("Talking about Camaroceras")
                        .language("en")
                        .temperature(0.5f)
                        .responseFormat(AudioResponseFormat.SRT).build())
                );
        return new ResponseEntity<>(response.getResult().getOutput(), HttpStatus.OK);
    }

}
