package com.spring.ai.firstproject.controllers;


import com.spring.ai.firstproject.service.DataLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UploadController {

    private final DataLoader dataloader;
    private final VectorStore vectorStore;

    private static final String UPLOAD_DIR = "uploads/";

    public UploadController(DataLoader dataloader, VectorStore vectorStore) {
        this.dataloader = dataloader;
        this.vectorStore = vectorStore;
    }

    @PostMapping(
            value = "/doupload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public Mono<ResponseEntity<Map<String, Object>>> uploadDocument(
            @RequestPart("file") FilePart filePart,
            @RequestPart("type") String type
    ) {

        Map<String, Object> response = new HashMap<>();

        // Validate document type
        if (!isValidDocumentType(type)) {
            response.put("success", false);
            response.put("message", "Invalid document type");
            return Mono.just(ResponseEntity.badRequest().body(response));
        }

        // Validate file name
        String originalFilename = filePart.filename();
        if (originalFilename == null ||
                !isValidFileExtension(originalFilename, type)) {

            response.put("success", false);
            response.put("message", "File extension does not match document type");
            return Mono.just(ResponseEntity.badRequest().body(response));
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);

        return Mono.fromCallable(() -> {
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    return uploadPath;
                })
                .flatMap(path -> {

                    String filename = System.currentTimeMillis() + "_" + originalFilename;
                    Path filePath = path.resolve(filename);

                    return filePart.transferTo(filePath)
                            .then(Mono.fromCallable(() -> {

                                // ⚠ If ingestDocument is blocking, wrap it
//                                List<Document> docs = dataloader
//                                        .ingestDocument(filePath.toFile(), vectorStore);
                                List<Document> docs = Mono.fromCallable(() ->
                                                dataloader.ingestDocument(filePath, vectorStore)
                                        ).subscribeOn(Schedulers.boundedElastic())
                                        .block();

                                response.put("success", true);
                                response.put("message", "File uploaded successfully");
                                response.put("filename", filename);
                                response.put("originalFilename", originalFilename);
                                response.put("type", type);
                                response.put("uploadPath", filePath.toString());
                                response.put("docs", docs.size());

                                return ResponseEntity.ok(response);
                            }));
                })
                .onErrorResume(e -> {
                    response.put("success", false);
                    response.put("message", "Upload failed: " + e.getMessage());
                    return Mono.just(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(response)
                    );
                });
    }

    // Validate document type
    private boolean isValidDocumentType(String type) {
        return type != null &&
                (type.equalsIgnoreCase("pdf")
                        || type.equalsIgnoreCase("text")
                        || type.equalsIgnoreCase("json"));
    }

    // Validate file extension
    private boolean isValidFileExtension(String filename, String type) {
        String lowercaseFilename = filename.toLowerCase();

        switch (type.toLowerCase()) {
            case "pdf":
                return lowercaseFilename.endsWith(".pdf");
            case "text":
                return lowercaseFilename.endsWith(".txt");
            case "json":
                return lowercaseFilename.endsWith(".json");
            default:
                return false;
        }
    }


}
