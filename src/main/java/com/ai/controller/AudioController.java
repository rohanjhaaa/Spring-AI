package com.ai.controller;

import com.openai.models.audio.AudioResponseFormat;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class AudioController {

    private final ChatClient gemmaChatClient;
    private final TranscriptionModel transcriptionModel;
    private final TextToSpeechModel textToSpeechModel;

    public AudioController(@Qualifier("gemmaChatMemoryClient") ChatClient gemmaChatClient, TranscriptionModel transcriptionModel,TextToSpeechModel textToSpeechModel) {
        System.out.println("Injected chat client = " +gemmaChatClient.getClass().getName());
        this.gemmaChatClient = gemmaChatClient;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
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

    @GetMapping("/speech")
    String speech(@RequestParam("message") String message) throws IOException {
        byte[] audioBytes = textToSpeechModel.call(message);
        Path path = Paths.get("output.mp3");
        Files.write(path, audioBytes);
        return "MP3 saved successfully to " + path.toAbsolutePath();
    }

    @GetMapping("/speech-options")
    String speechWithOptions(@RequestParam("message") String message) throws IOException {
        TextToSpeechResponse speechResponse = textToSpeechModel.call(new TextToSpeechPrompt(message,
                TextToSpeechOptions.builder().voice(OpenAiAudioSpeechOptions.Voice.NOVA.getValue())
                        .speed(2.0)
                        .format(OpenAiAudioSpeechOptions.AudioResponseFormat.MP3.getValue()).build()));
        Path path = Paths.get("speech-options.mp3");
        Files.write(path, speechResponse.getResult().getOutput());
        return "MP3 saved successfully to " + path.toAbsolutePath();
    }

}
