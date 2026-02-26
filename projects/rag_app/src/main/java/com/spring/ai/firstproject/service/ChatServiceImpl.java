package com.spring.ai.firstproject.service;

import com.spring.ai.firstproject.config.ConfidenceBasedHybridRetriever;
import com.spring.ai.firstproject.config.SearxngDocumentRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Autowired
    private ChatMemory chatMemory;

    public ChatServiceImpl(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    /* ============================================================
       PUBLIC API
       ============================================================ */

    @Override
    public Flux<String> streamResponse(String userQuery, String userId) {

        RetrievalAugmentationAdvisor ragAdvisor = buildAdvisor(userId);

        MessageChatMemoryAdvisor memoryAdvisor =
                MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(userId)
                        .build();

        return chatClient
                .prompt()
                .advisors(ragAdvisor, memoryAdvisor)
                .user(userQuery)
                .stream()
                .content()
                .bufferTimeout(25, Duration.ofMillis(200))
                .map(chunks -> String.join("", chunks))
                .filter(s -> !s.isBlank());
    }

    @Override
    public String chatTemplate(String query) {
        return "";
    }

    @Override
    public Flux<String> streamChat(String query) {
        return null;
    }

    @Override
    public void saveData(List<String> data) {
        List<Document> docs = data.stream()
                .map(Document::new)
                .toList();
        vectorStore.add(docs);
    }

    @Override
    public String getResponse(String userQuery, String userId) {
        return "";
    }

    /* ============================================================
       CORE RAG ADVISOR (LOCAL → WEB FALLBACK)
       ============================================================ */

    private RetrievalAugmentationAdvisor buildAdvisor(String userId) {

        DocumentRetriever vectorRetriever =
                VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .topK(4)
                        .similarityThreshold(0.55)
                        .build();

        DocumentRetriever webRetriever =
                new SearxngDocumentRetriever();

        DocumentRetriever hybridRetriever =
                new ConfidenceBasedHybridRetriever(
                        vectorRetriever,
                        webRetriever
                );

        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(rewriteQueryTransformer())
                .documentRetriever(hybridRetriever)
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(
                        ContextualQueryAugmenter.builder()
                                .allowEmptyContext(false)
                                .build()
                )
                .build();
    }

    /* ============================================================
       QUERY REWRITE (ONLY ONCE – SAFE)
       ============================================================ */

    private RewriteQueryTransformer rewriteQueryTransformer() {
        return RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClient.mutate().clone())
                .build();
    }
}