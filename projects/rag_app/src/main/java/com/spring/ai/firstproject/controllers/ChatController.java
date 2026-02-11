package com.spring.ai.firstproject.controllers;

import com.spring.ai.firstproject.service.ChatService;
//import org.apache.coyote.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping
public class ChatController {

//    private ChatService chatService;
//
//    public ChatController(ChatService chatService) {
//        this.chatService = chatService;
//    }
//
//    @GetMapping("/chat")
//    public ResponseEntity<String> chat(
//            @RequestParam(value = "q", required = true) String q,
//            @RequestHeader("userId") String userId
//            ) {
//        return ResponseEntity.ok(chatService.chatTemplate(q,userId));
//    }
//
//    @GetMapping(value = "/stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public ResponseEntity<Flux<String>> streamChat(
//            @RequestParam(value = "q", required = false) String query
//    ) {
//        if (query == null || query.isBlank()) {
//            return ResponseEntity.ok(Flux.just("Please type a message."));
//        }
//        return ResponseEntity.ok(this.chatService.streamChat(query));
//    }




}
