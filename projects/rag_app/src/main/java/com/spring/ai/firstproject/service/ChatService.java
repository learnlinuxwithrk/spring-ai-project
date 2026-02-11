package com.spring.ai.firstproject.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    String chatTemplate(String query);


    Flux<String> streamChat(String query);

    void saveData(List<String> list);
    
    String getResponse(String userQuery,String userId);

    Flux<String> streamResponse(String userQuery, String userId);

}
