package com.spring.ai.firstproject.controllers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.spring.ai.firstproject.service.ChatService;

import reactor.core.publisher.Flux;

@Controller
public class HomeController {

    private final ChatService chatService;

    public HomeController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Serve chat page
    @GetMapping("/home")
    public String chatPage() {
        return "chat"; // Thymeleaf template
    }

//    // Stream chat responses
//    @GetMapping(value = "/stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public ResponseEntity<Flux<String>> streamChat(
//            @RequestParam(value = "q", required = false) String query
//    ) {
//        if (query == null || query.isBlank()) {
//            return ResponseEntity.ok(Flux.just("Please type a message."));
//        }
//
//        // Stream response word by word
//        return ResponseEntity.ok(
//                chatService.streamChat(query)
//                        .map(chunk -> chunk.replace("data:", "").trim()) // remove unwanted prefix
//                        .filter(s -> !s.isEmpty())
//        );
//    }
    
    @GetMapping(path = "/answerme", produces = "text/event-stream")
    public Flux<ServerSentEvent<String>> streamChat(@RequestParam("q") String q) {

        // Get the bot response as a plain string
        String botResponse = chatService.chatTemplate(q); // must return String

        // Split response into words for streaming
        List<String> words = Arrays.asList(botResponse.split(" "));

        // Stream each word with a tiny delay
        return Flux.fromIterable(words)
                   .delayElements(Duration.ofMillis(100))
                   .map(word -> ServerSentEvent.builder(word).build());
    }
//
//  @GetMapping("/answerme")
//  public ResponseEntity<String> chat(
//          @RequestParam(value = "q", required = true) String q) {
//      return ResponseEntity.ok(chatService.chatTemplate(q));
//  }
}
