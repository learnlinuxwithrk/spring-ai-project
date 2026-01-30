package com.spring.ai.firstproject.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {
//
//	@Autowired
//	private VectorStore vectorStore;
//
//	public void uploadPdf(MultipartFile file) throws IOException {
//
//		String rawText = extractPdfText(file);
//		String cleanText = normalize(rawText);
//
//		List<String> chunks = chunkText(cleanText);
//
//		storePdf(file.getOriginalFilename(), chunks);
//	}
//
//	public void storePdf(String pdfName, List<String> chunks) {
//
//		List<Document> documents = chunks.stream().map(chunk -> new Document(chunk, Map.of("source", pdfName)))
//				.toList();
//
//		vectorStore.add(documents);
//	}
//
//	public List<String> chunkText(String text) {
//		int chunkSize = 500;
//		int overlap = 50;
//
//		List<String> chunks = new ArrayList<>();
//		for (int i = 0; i < text.length(); i += (chunkSize - overlap)) {
//			int end = Math.min(text.length(), i + chunkSize);
//			chunks.add(text.substring(i, end));
//		}
//		return chunks;
//	}
//
//	private String normalize(String text) {
//		return text.replaceAll("\\s+", " ").trim();
//	}
//
//	public String extractPdfText(MultipartFile file) throws IOException {
//		PDDocument document = PDDocument.load(file.getInputStream());
//		PDFTextStripper stripper = new PDFTextStripper();
//		String text = stripper.getText(document);
//		document.close();
//		return text;
//	}

}
