package com.spring.ai.firstproject.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataLoaderImpl implements DataLoader {

	@Value("classpath:waqf_progress_report.json")
	private Resource jsonResource;

	private Resource pdfResource;

	private static final Logger log = LoggerFactory.getLogger(DataLoaderImpl.class);

	// Safe chunk config
	private static final int CHUNK_SIZE = 1500;
	private static final int CHUNK_OVERLAP = 200;

	private VectorStore vectorStore;

	/**
	 * Load JSON documents from the sample JSON file. Each "event" becomes a
	 * separate Document.
	 */

	public List<Document> loadDocumentsFromJsonNEW() {
		List<Document> documents = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(jsonResource.getInputStream());

			for (JsonNode stateNode : root) {
				String state = stateNode.path("state_waqf_board").asText();
				int waqfInitiated = stateNode.path("waqf_property_initiated_for_uploading").asInt();
				int makersSubmitted = stateNode.path("makers_submitted").asInt();
				int checkersSubmitted = stateNode.path("checkers_submitted").asInt();
				int approversApproved = stateNode.path("approvers_approved").asInt();
				int rejected = stateNode.path("rejected_property").asInt();
				int pending = stateNode.path("pending_for_approval").asInt();
				int totalInitiated = stateNode.path("total_no_of_waqf_property_initiated_till_date").asInt();

				// Build readable text
				String text = String.format(
						"%s Waqf Progress report:\n" + "Waqf Property Initiated: %d\n" + "Makers Submitted: %d\n"
								+ "Checkers Submitted: %d\n" + "Approvers Approved: %d\n" + "Rejected Properties: %d\n"
								+ "Pending for Approval: %d\n" + "Total Properties Initiated: %d",
						state, waqfInitiated, makersSubmitted, checkersSubmitted, approversApproved, rejected, pending,
						totalInitiated);

				Document doc = new Document(text);

				// ✅ Add metadata for accurate matching
				// doc.getMetadata().put("state_waqf_board", state); // display name
				doc.getMetadata().put("state", state.toLowerCase()); // lowercase for exact matching
				doc.getMetadata().put("waqf_property_initiated_for_uploading", waqfInitiated);
				doc.getMetadata().put("makers_submitted", makersSubmitted);
				doc.getMetadata().put("checkers_submitted", checkersSubmitted);
				doc.getMetadata().put("approvers_approved", approversApproved);
				doc.getMetadata().put("rejected_property", rejected);
				doc.getMetadata().put("pending_for_approval", pending);
				doc.getMetadata().put("total_no_of_waqf_property_initiated_till_date", totalInitiated);

				documents.add(doc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return documents;
	}

	public List<Document> loadDocumentsFromJson() {

		List<Document> documents = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(jsonResource.getInputStream());

			for (JsonNode stateNode : root) {
				String state = stateNode.path("state_waqf_board").asText();
				int waqfInitiated = stateNode.path("waqf_property_initiated_for_uploading").asInt();
				int makersSubmitted = stateNode.path("makers_submitted").asInt();
				int checkersSubmitted = stateNode.path("checkers_submitted").asInt();
				int approversApproved = stateNode.path("approvers_approved").asInt();
				int rejected = stateNode.path("rejected_property").asInt();
				int pending = stateNode.path("pending_for_approval").asInt();
				int totalInitiated = stateNode.path("total_no_of_waqf_property_initiated_till_date").asInt();

				// Build readable text
				String text = String.format(
						"%s Waqf Progress:\n" + "Waqf Property Initiated: %d\n" + "Makers Submitted: %d\n"
								+ "Checkers Submitted: %d\n" + "Approvers Approved: %d\n" + "Rejected Properties: %d\n"
								+ "Pending for Approval: %d\n" + "Total Properties Initiated: %d",
						state, waqfInitiated, makersSubmitted, checkersSubmitted, approversApproved, rejected, pending,
						totalInitiated);

				Document doc = new Document(text);

				// ✅ Important: use exact JSON key for metadata
				doc.getMetadata().put("state_waqf_board", state);
				doc.getMetadata().put("waqf_property_initiated_for_uploading", waqfInitiated);
				doc.getMetadata().put("makers_submitted", makersSubmitted);
				doc.getMetadata().put("checkers_submitted", checkersSubmitted);
				doc.getMetadata().put("approvers_approved", approversApproved);
				doc.getMetadata().put("rejected_property", rejected);
				doc.getMetadata().put("pending_for_approval", pending);
				doc.getMetadata().put("total_no_of_waqf_property_initiated_till_date", totalInitiated);

				documents.add(doc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return documents;
	}

	public List<Document> loadDocumentsFromJsonDefault() {
		List<Document> documents = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(jsonResource.getInputStream());

			for (JsonNode event : root.path("events")) {
				String text = String.format("Event name: %s. Event date: %s.", event.path("name").asText(),
						event.path("date").asText());

				Document doc = new Document(text);
				doc.getMetadata().put("eventId", event.path("id").asInt());
				documents.add(doc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("JSON Document Size : " + documents.size());
		documents.forEach(d -> System.out.println("ITEM " + d));
		return documents;
	}

	/**
	 * Load PDF documents using PagePdfDocumentReader. Each page becomes a separate
	 * Document.
	 */
	public List<Document> loadDocumentsFromPdf() {
		var pdfReader = new PagePdfDocumentReader(pdfResource,
				PdfDocumentReaderConfig.builder().withPageTopMargin(0).withPageExtractedTextFormatter(
						ExtractedTextFormatter.builder().withNumberOfTopTextLinesToDelete(0).build()).build());

		List<Document> docs = pdfReader.read();
		System.out.println("PDF Document Size : " + docs.size());
		docs.forEach(d -> System.out.println("ITEM " + d));
		return docs;
	}

	/**
	 * Step 1: ingest only JSON documents into vector store
	 */
	public List<Document> ingestJsonIntoVectorStore(VectorStore vectorStore) {
		List<Document> jsonDocs = loadDocumentsFromJson();
		vectorStore.add(jsonDocs);

		System.out.println("JSON documents ingested into vector store: " + jsonDocs.size());
		return jsonDocs;
	}

	/**
	 * Step 2: ingest only PDF documents into vector store
	 */
	public List<Document> ingestPdfIntoVectorStore(VectorStore vectorStore) {
		List<Document> pdfDocs = loadDocumentsFromPdf();
		vectorStore.add(pdfDocs);
		System.out.println("PDF documents ingested into vector store: " + pdfDocs.size());
		return pdfDocs;

	}

    public List<Document> ingestDocument(Path filePath, VectorStore vectorStore) {

        try {

            if (filePath == null || !Files.exists(filePath)) {
                throw new IllegalArgumentException("Uploaded file is invalid.");
            }

            String filename = filePath.getFileName().toString().toLowerCase();
            List<Document> documents;

            if (filename.endsWith(".pdf")) {
                documents = ingestPdf(filePath);
            }
            else if (filename.endsWith(".txt") || filename.endsWith(".json")) {
                documents = ingestText(filePath);
            }
            else {
                throw new IllegalArgumentException("Unsupported file type: " + filename);
            }

            log.info("Original documents count: {}", documents.size());

            // 🔥 Split before embedding
            List<Document> chunks = splitDocuments(documents);

            log.info("Total chunks created: {}", chunks.size());

            vectorStore.add(chunks);

            log.info("Successfully inserted {} chunks into vector store.", chunks.size());

            return chunks;

        } catch (Exception e) {
            log.error("Document ingestion failed", e);
            throw new RuntimeException("Ingestion failed: " + e.getMessage());
        }
    }

    private List<Document> ingestText(Path filePath) throws IOException {

        String text = Files.readString(filePath, StandardCharsets.UTF_8);

        Document doc = new Document(text);

        doc.getMetadata().put("source", "uploaded-text");
        doc.getMetadata().put("filename", filePath.getFileName().toString());

        return List.of(doc);
    }

	/**
	 * ================================ SAFE DOCUMENT SPLITTER
	 * ================================
	 */
	private List<Document> splitDocuments(List<Document> documents) {

		List<Document> result = new ArrayList<>();

		for (Document doc : documents) {

			String text = doc.getText();

			if (text == null || text.isBlank()) {
				continue;
			}

			int start = 0;

			while (start < text.length()) {

				int end = Math.min(start + CHUNK_SIZE, text.length());
				String chunkText = text.substring(start, end);

				Document chunk = new Document(chunkText);
				chunk.getMetadata().putAll(doc.getMetadata());

				result.add(chunk);

				start += CHUNK_SIZE - CHUNK_OVERLAP;
			}
		}

		return result;
	}

    private List<Document> ingestPdf(Path filePath) throws Exception {

        Resource resource = new FileSystemResource(filePath);

        PagePdfDocumentReader reader =
                new PagePdfDocumentReader(
                        resource,
                        PdfDocumentReaderConfig.builder()
                                .withPageExtractedTextFormatter(
                                        ExtractedTextFormatter.builder().build()
                                )
                                .build()
                );

        List<Document> rawDocs = reader.read();

        System.out.println("File Name: " + filePath.getFileName());

        List<Document> cleanDocs = new ArrayList<>();

        int pageNumber = 1;

        for (Document rawDoc : rawDocs) {

            String text = rawDoc.getText();

            if (text == null || text.isBlank()) {
                continue;
            }

            Map<String, Object> metadata = new HashMap<>();

            metadata.put("source", "uploaded-pdf");
            metadata.put("filename", filePath.getFileName().toString());
            metadata.put("page_number", pageNumber++);

            Document cleanDoc = new Document(text, metadata);

            cleanDocs.add(cleanDoc);
        }

        System.out.println("Total Clean Pages: " + cleanDocs.size());

        return cleanDocs;
    }


//	private List<Document> ingestPdf(MultipartFile file) throws Exception {
//
//		Resource resource = new InputStreamResource(file.getInputStream());
//
//		PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig.builder()
//				.withPageExtractedTextFormatter(ExtractedTextFormatter.builder().build()).build());
//
//		List<Document> docs = reader.read();
//
//		System.out.println("File Name : "+file.getOriginalFilename());
//		docs.forEach(doc -> {
//			doc.getMetadata().put("source", "uploaded-pdf");
//			doc.getMetadata().put("filename", file.getOriginalFilename());
//		});
//
//		return docs;
//	}

}
