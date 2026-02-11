package com.spring.ai.firstproject.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.spring.ai.firstproject.service.ChatService;
import com.spring.ai.firstproject.service.DataLoader;
import com.spring.ai.firstproject.service.PdfService;
import com.spring.ai.firstproject.dao.Users;
import reactor.core.publisher.Flux;

//@Controller
public class HomeController {

//	private final ChatService chatService;
//
//	private final PdfService pdfService;
//
//	public final DataLoader dataloader;
//
//	public final VectorStore vectorStore;
//
//	// Directory where uploaded files will be stored
//	private static final String UPLOAD_DIR = "uploads/";
//
//	 // In-memory user storage (replace with database in production)
//    private static final Map<String, Users> users = new HashMap<>();
//
//    static {
//        // Demo users
//        users.put("admin", new Users("admin", "admin123", "John Doe", "Administrator"));
//        users.put("user", new Users("user", "user123", "Jane Smith", "User"));
//    }
//
//
//	public HomeController(ChatService chatService, PdfService pdfService, DataLoader dataloader,
//			VectorStore vectorStore) {
//		this.chatService = chatService;
//		this.pdfService = pdfService;
//		this.dataloader = dataloader;
//		this.vectorStore = vectorStore;
//	}
//
//
//	 @GetMapping("/")
//	    public String chatPage() {
//	        return "chat-enhanced";
//	    }
//
//	  @GetMapping("/upload")
//	    public String uploadPage() {
//	        return "upload-enhanced";
//	    }
//
////@GetMapping(path = "/chatme", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
////public Flux<ServerSentEvent<String>> streamChat(
////        @RequestParam String q,
////        @RequestParam String userId
////) {
////
////    return chatService
////            .streamResponse(q, userId)
////            .map(chunk ->
////                    ServerSentEvent.builder(chunk).build()
////            )
////            .concatWith(
////                    Flux.just(
////                            ServerSentEvent.builder("")
////                                    .event("done")
////                                    .build()
////                    )
////            );
////}
//
//	// API Endpoint - Handles file upload
//	@PostMapping("/doUpload")
//	@ResponseBody
//	public ResponseEntity<?> uploadDocumentold(@RequestParam("file") MultipartFile file,
//			@RequestParam("type") String type) {
//
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			// Validate file
//			if (file.isEmpty()) {
//				response.put("success", false);
//				response.put("message", "Please select a file to upload");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//			// Validate file size (10MB limit)
//			long maxSize = 10 * 1024 * 1024; // 10MB
//			if (file.getSize() > maxSize) {
//				response.put("success", false);
//				response.put("message", "File size exceeds 10MB limit");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//			// Validate document type
//			if (!isValidDocumentType(type)) {
//				response.put("success", false);
//				response.put("message", "Invalid document type");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//			// Validate file extension matches document type
//			String originalFilename = file.getOriginalFilename();
//			if (originalFilename == null || !isValidFileExtension(originalFilename, type)) {
//				response.put("success", false);
//				response.put("message", "File extension does not match document type");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//			// Create upload directory if it doesn't exist
//			Path uploadPath = Paths.get(UPLOAD_DIR);
//			if (!Files.exists(uploadPath)) {
//				Files.createDirectories(uploadPath);
//			}
//
//			// Generate unique filename
//			String filename = System.currentTimeMillis() + "_" + originalFilename;
//			Path filePath = uploadPath.resolve(filename);
//
//			// Save file
//			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//			List<Document> docs = dataloader.ingestDocument(file, vectorStore);
//
//			// Build success response
//			response.put("success", true);
//			response.put("message", "File uploaded successfully");
//			response.put("filename", filename);
//			response.put("originalFilename", originalFilename);
//			response.put("type", type);
//			response.put("size", file.getSize());
//			response.put("contentType", file.getContentType());
//			response.put("uploadPath", filePath.toString());
//			response.put("docs", docs.size());
//
//			return ResponseEntity.ok(response);
//
//		} catch (IOException e) {
//			response.put("success", false);
//			response.put("message", "Failed to upload file: " + e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		} catch (Exception e) {
//			response.put("success", false);
//			response.put("message", "An unexpected error occurred: " + e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}
//
//	// Validate document type
//	private boolean isValidDocumentType(String type) {
//		return type != null && (type.equals("pdf") || type.equals("text") || type.equals("json"));
//	}
//
//	// Validate file extension
//	private boolean isValidFileExtension(String filename, String type) {
//		String lowercaseFilename = filename.toLowerCase();
//		switch (type) {
//		case "pdf":
//			return lowercaseFilename.endsWith(".pdf");
//		case "text":
//			return lowercaseFilename.endsWith(".txt");
//		case "json":
//			return lowercaseFilename.endsWith(".json");
//		default:
//			return false;
//		}
//	}
//
// // ==================== API ENDPOINTS ====================
//
//    /**
//     * Login API
//     */
//    @PostMapping("/login")
//    @ResponseBody
//    public ResponseEntity<?> login(@RequestParam String username,
//                                   @RequestParam String password,
//                                   @RequestParam(required = false) Boolean remember) {
//        Map<String, Object> response = new HashMap<>();
//
//        Users user = users.get(username);
//        if (user != null && user.password.equals(password)) {
//            // Generate simple token (use JWT in production)
//            String token = UUID.randomUUID().toString();
//
//            response.put("success", true);
//            response.put("message", "Login successful");
//            response.put("token", token);
//
//            Map<String, String> userInfo = new HashMap<>();
//            userInfo.put("username", user.username);
//            userInfo.put("name", user.name);
//            userInfo.put("role", user.role);
//            response.put("user", userInfo);
//
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "Invalid username or password");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//        }
//    }
//
//    /**
//     * Document Upload API
//     */
//    @PostMapping("/doupload")
//    @ResponseBody
//    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file,
//                                           @RequestParam("type") String type) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            // Validate file
//            if (file.isEmpty()) {
//                response.put("success", false);
//                response.put("message", "Please select a file to upload");
//                return ResponseEntity.badRequest().body(response);
//            }
//
//            // Validate file size (10MB limit)
//            long maxSize = 10 * 1024 * 1024;
//            if (file.getSize() > maxSize) {
//                response.put("success", false);
//                response.put("message", "File size exceeds 10MB limit");
//                return ResponseEntity.badRequest().body(response);
//            }
//
//            // Validate document type
//            if (!isValidDocumentType(type)) {
//                response.put("success", false);
//                response.put("message", "Invalid document type");
//                return ResponseEntity.badRequest().body(response);
//            }
//
//            // Validate file extension
//            String originalFilename = file.getOriginalFilename();
//            if (originalFilename == null || !isValidFileExtension(originalFilename, type)) {
//                response.put("success", false);
//                response.put("message", "File extension does not match document type");
//                return ResponseEntity.badRequest().body(response);
//            }
//
//            // Create upload directory
//            Path uploadPath = Paths.get(UPLOAD_DIR);
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            // Generate unique filename
//            String filename = System.currentTimeMillis() + "_" + originalFilename;
//            Path filePath = uploadPath.resolve(filename);
//
//            // Save file
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            List<Document> docs = dataloader.ingestDocument(file, vectorStore);
//
//			// Build success response
//			response.put("success", true);
//			response.put("message", "File uploaded successfully");
//			response.put("filename", filename);
//			response.put("originalFilename", originalFilename);
//			response.put("type", type);
//			response.put("size", file.getSize());
//			response.put("contentType", file.getContentType());
//			response.put("uploadPath", filePath.toString());
//			response.put("docs", docs.size());
//
//			return ResponseEntity.ok(response);
//
//        } catch (IOException e) {
//            response.put("success", false);
//            response.put("message", "Failed to upload file: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//


}
