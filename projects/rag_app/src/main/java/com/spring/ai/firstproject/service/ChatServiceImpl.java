package com.spring.ai.firstproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ChatClient chatClient;
    
    @Autowired
    private ChatMemory  chatMemory;


    @Value("classpath:/prompts/user-message.st")
    private Resource userMessage;

    @Value("classpath:/prompts/system-message.st")
    private Resource systemMessage;


    private VectorStore vectorStore;

    public ChatServiceImpl(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }


    @Override
    public String chatTemplate(String query) {

        //load data from vector database
//
//        SearchRequest searchRequest = SearchRequest.builder()
//                .topK(5)
//                .similarityThreshold(0.6)
//                .query(query)
//                .build();
//
//        List<Document> documents = this.vectorStore.similaritySearch(searchRequest);
//        List<String> documentList = documents.stream().map(Document::getText).toList();
//        String contextData = String.join(", ", documentList);
//        logger.info("Context Data: {}", contextData);
//
//        // similar result user query

        // pass in context

        var advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever
                        .builder()
                        .vectorStore(this.vectorStore)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder().allowEmptyContext(true).build())
                .build();


        return this.chatClient
                .prompt()
                .advisors(advisor)

//                .advisors(new SimpleLoggerAdvisor())
//                .system(system ->
//                        system.text(this.systemMessage).param("documents", contextData))
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
//                .advisors(
//                        QuestionAnswerAdvisor
//                                .builder(vectorStore)
//                                .searchRequest(SearchRequest.builder()
//                                        .topK(3)
//                                        .similarityThreshold(0.5)
//                                        .build())
//                                .build())
                .user(user ->

                        user.text(this.userMessage).param("query", query))
                .call()
                .content()
                ;
    }

    @Override
    public Flux<String> streamChat(String query) {

        return this.chatClient
                .prompt()
                .system(system -> system.text(this.systemMessage))
                .user(user -> user.text(this.userMessage).param("query", query))
                .stream()
                .content();


    }

    @Override
    public void saveData(List<String> list) {

        List<Document> documentList = list.stream().map(Document::new).toList();
        this.vectorStore.add(documentList);


    }
    
    @Override
    public String getResponse(String userQuery,String userId) {
    	
    	var advisor = RetrievalAugmentationAdvisor.builder()
    	        .queryTransformers(
    	                RewriteQueryTransformer.builder()
    	                        .chatClientBuilder(chatClient.mutate().clone())
    	                        .build()
    	        )
    	        .documentRetriever(
    	                VectorStoreDocumentRetriever.builder()
    	                        .vectorStore(vectorStore)
    	                        .topK(3)
    	                        .similarityThreshold(0.5)
    	                        .build()
    	        )
    	        .documentJoiner(new ConcatenationDocumentJoiner())
    	        .queryAugmenter(ContextualQueryAugmenter.builder().build())
    	       // .documentPostProcessors()
    	        .build();

    	 // 2️⃣ Chat Memory Advisor
        var memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(userId)
                .build();
        return chatClient
                .prompt()
                .advisors(advisor)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,userId))                
                .user(userQuery)
                .call()
                .content();
    }


    public Flux<String> streamResponse(String userQuery, String userId) {
        var advisor = buildAdvisor(userId);

        return chatClient
                .prompt()
                .advisors(advisor)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .user(userQuery)
                .stream()
                .content()
                .bufferTimeout(25, Duration.ofMillis(200)) // buffer tokens
                .map(chunks -> String.join("", chunks))    // join safely
                .filter(s -> !s.isBlank());
//        return Mono.fromCallable(() -> {
//                    // This is blocking, so we execute on boundedElastic
//                    return chatClient
//                            .prompt()
//                            .advisors(advisor)
//                            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
//                            .user(userQuery)
//                            .call()    // blocking
//                            .content();
//                })
//                .subscribeOn(Schedulers.boundedElastic()) // move to a thread that allows blocking
//                .flux(); // convert Mono<String> to Flux<String> for SSE
    }


    public Flux<String> streamResponse1(String userQuery, String userId) {

        var advisor = buildAdvisor(userId);

        return chatClient
                .prompt()
                .advisors(advisor)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .user(userQuery)
                .stream()   // VERY IMPORTANT
                .content(); // returns Flux<String>
    }

    private RetrievalAugmentationAdvisor buildAdvisor(String userId) {



        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(
                        RewriteQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                            .build(),
                        TranslationQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate())
                                .targetLanguage("hindi")
                                .build()

                )

                .queryExpander(MultiQueryExpander.builder()
                        .chatClientBuilder(chatClient.mutate()) // just mutate, no clone
                        .numberOfQueries(2) // example: 3 expansions
                        .includeOriginal(true)
                        .build())

                .documentRetriever(
                        VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .topK(30)
                                .similarityThreshold(0.15)
                                .build()
                )
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(ContextualQueryAugmenter.builder().build())
                //.documentPostProcessors()
                .build();
    }




}
