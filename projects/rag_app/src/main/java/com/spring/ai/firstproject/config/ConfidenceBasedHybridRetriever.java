package com.spring.ai.firstproject.config;


import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.List;

public class ConfidenceBasedHybridRetriever implements DocumentRetriever {

    private final DocumentRetriever vectorRetriever;
    private final DocumentRetriever webRetriever;

    public ConfidenceBasedHybridRetriever(
            DocumentRetriever vectorRetriever,
            DocumentRetriever webRetriever) {
        this.vectorRetriever = vectorRetriever;
        this.webRetriever = webRetriever;
    }

    @Override
    public List<Document> retrieve(Query query) {

        List<Document> localDocs = vectorRetriever.retrieve(query);

        if (localDocs != null && !localDocs.isEmpty()) {
            return localDocs;
        }

        return webRetriever.retrieve(query);
    }
}