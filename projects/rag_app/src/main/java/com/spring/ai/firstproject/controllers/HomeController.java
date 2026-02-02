package com.spring.ai.firstproject.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.spring.ai.firstproject.service.ChatService;
import com.spring.ai.firstproject.service.DataLoader;
import com.spring.ai.firstproject.service.PdfService;

@Controller
public class HomeController {

	private final ChatService chatService;

	private final PdfService pdfService;

	public final DataLoader dataloader;

	public HomeController(ChatService chatService, PdfService pdfService, DataLoader dataloader) {
		this.chatService = chatService;
		this.pdfService = pdfService;
		this.dataloader = dataloader;
	}

	@GetMapping("/")
	public String chatPage() {
		return "chat";
	}

	@GetMapping("/answerme")
	public ResponseEntity<StreamingResponseBody> streamChat(@RequestParam("q") String q) {

		String botResponse = chatService.chatTemplate(q); // Must return String

		List<String> words = Arrays.asList(botResponse.split(" "));

		StreamingResponseBody stream = outputStream -> {
			try {
				for (String word : words) {
					// SSE format: data: word\n\n
					String sseData = "data: " + word + "\n\n";
					outputStream.write(sseData.getBytes());
					outputStream.flush();
					Thread.sleep(100); // small delay for streaming effect
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		};

		return ResponseEntity.ok().header("Content-Type", "text/event-stream").body(stream);
	}

//    @PostMapping("/upload-pdf")
//    public ResponseEntity<String> uploadPdf(
//            @RequestParam("file") MultipartFile file) throws IOException {
//
//        pdfService.uploadPdf(file);
//        return ResponseEntity.ok("PDF indexed successfully");
//    }

	// Rest API to get response

//    @PostMapping("/chatme")
//    public ResponseEntity<String> getResponse(@RequestParam("q") String userQuery){
//        return ResponseEntity.ok(chatService.getResponse(userQuery));
//    }
//    

	@GetMapping("/chatme")
	public ResponseEntity<StreamingResponseBody> streamChatMe(@RequestParam("q") String userQuery,
			@RequestParam("userId") String userId) {

		System.out.println("User ID :" + userId + " Questions :" + userQuery);

		String botResponse = chatService.getResponse(userQuery, userId);

		System.out.println("botResponse " + botResponse);

		// Split by newline instead of space
		List<String> lines = Arrays.asList(botResponse.split("\\r?\\n"));

		StreamingResponseBody stream = outputStream -> {
			try {
				for (String line : lines) {
					String sseData = "data: " + line + "\n\n";
					outputStream.write(sseData.getBytes());
					outputStream.flush();
					Thread.sleep(50); // Optional delay for "streaming" effect
				}
				// Signal end of stream
				String done = "event: done\ndata: \n\n";
				outputStream.write(done.getBytes());
				outputStream.flush();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		};

		return ResponseEntity.ok().header("Content-Type", "text/event-stream").body(stream);
	}

	@GetMapping("/chatmeold")
	public ResponseEntity<StreamingResponseBody> streamChatMeOLD(@RequestParam("q") String userQuery,
			@RequestParam("userId") String userId) {

		String botResponse = chatService.getResponse(userQuery, userId);

		System.out.println("botResponse " + botResponse);
		List<String> words = Arrays.asList(botResponse.split(" "));

		StreamingResponseBody stream = outputStream -> {
			try {
				for (String word : words) {
					String sseData = "data: " + word + "\n\n";
					outputStream.write(sseData.getBytes());
					outputStream.flush();
					Thread.sleep(50);
				}
				String done = "event: done\ndata: \n\n";
				outputStream.write(done.getBytes());
				outputStream.flush();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		};

		return ResponseEntity.ok().header("Content-Type", "text/event-stream")
//                .header("Cache-Control", "no-cache")
				.body(stream);
	}

}
