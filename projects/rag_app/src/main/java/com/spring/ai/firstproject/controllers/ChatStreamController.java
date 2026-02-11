package com.spring.ai.firstproject.controllers;

import com.spring.ai.firstproject.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@RestController
public class ChatStreamController {

    private final ChatService chatService;

    public ChatStreamController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping(value = "/chatme", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(
            @RequestParam String q,
            @RequestParam String userId
    ) {
        return chatService.streamResponse(q, userId)
                // Only null check, DO NOT filter blanks
                .filter(Objects::nonNull)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build())
                .concatWith(Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
    }








}
