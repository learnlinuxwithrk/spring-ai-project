package com.spring.ai.firstproject.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataLoader {

	List<Document> loadDocumentsFromJson();

	List<Document> loadDocumentsFromPdf();

	List<Document> ingestJsonIntoVectorStore(VectorStore vectorStore);
	List<Document> ingestPdfIntoVectorStore(VectorStore vectorStore);
	
	List<Document> ingestDocument(MultipartFile file,VectorStore vectorStore);

}
