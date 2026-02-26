package com.spring.ai.firstproject.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.ArrayList;
import java.util.List;

public class HybridDocumentRetriever implements DocumentRetriever {

    private final DocumentRetriever vectorRetriever;
    private final DocumentRetriever webRetriever;

    public HybridDocumentRetriever(
            DocumentRetriever vectorRetriever,
            DocumentRetriever webRetriever) {
        this.vectorRetriever = vectorRetriever;
        this.webRetriever = webRetriever;
    }

    @Override
    public List<Document> retrieve(Query query) {

        List<Document> vectorDocs = vectorRetriever.retrieve(query);
        List<Document> webDocs = webRetriever.retrieve(query);

        List<Document> allDocs = new ArrayList<>();
        allDocs.addAll(vectorDocs);
        allDocs.addAll(webDocs);

        return allDocs;
    }
}