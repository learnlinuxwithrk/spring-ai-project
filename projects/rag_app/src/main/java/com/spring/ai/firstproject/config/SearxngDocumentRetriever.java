package com.spring.ai.firstproject.config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SearxngDocumentRetriever implements DocumentRetriever {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<org.springframework.ai.document.Document> retrieve(Query query) {

        String userQuery = query.text();

        // Restrict search to official Indian government domains
        String enhancedQuery = userQuery +" Gov Site: gov.in";

        System.out.println("🌐 Calling SearXNG for: " + enhancedQuery);

        String url = "http://localhost:8888/search?q=" +
                UriUtils.encode(enhancedQuery, StandardCharsets.UTF_8) +
                "&format=json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.getBody().get("results");

        List<org.springframework.ai.document.Document> documents = new ArrayList<>();

        if (results != null) {
            for (int i = 0; i < Math.min(3, results.size()); i++) {

                String title = (String) results.get(i).get("title");
                String urlLink = (String) results.get(i).get("url");

                if (urlLink == null || !urlLink.contains(".gov.in")) {
                    continue;
                }

                try {

                    ResponseEntity<String> pageResponse =
                            restTemplate.getForEntity(urlLink, String.class);

                    String html = pageResponse.getBody();

                    // Parse using Jsoup
                    org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(html);

                    // Remove unwanted elements
                    jsoupDoc.select("script, style, nav, footer, header, noscript").remove();
                    jsoupDoc.select(".menu, .navbar, .breadcrumb, .sidebar, .ads, .carousel").remove();

                    // Extract structured content
                    Element mainContent = jsoupDoc.selectFirst(
                            "main, #content, .content, .main-content"
                    );

                    String extractedText;

                    if (mainContent != null) {
                        extractedText = mainContent.text();
                    } else {
                        extractedText = jsoupDoc.body().text();
                    }

                    extractedText = extractedText.replaceAll("\\s+", " ").trim();

                    if (extractedText.length() < 200) {
                        continue;
                    }

                    extractedText = extractedText.substring(
                            0,
                            Math.min(4000, extractedText.length())
                    );

                    org.springframework.ai.document.Document document =
                            new org.springframework.ai.document.Document(extractedText);

                    document.getMetadata().put("source", urlLink);
                    document.getMetadata().put("title", title);

                    documents.add(document);

                } catch (Exception e) {
                    System.out.println("❌ Failed to fetch page: " + urlLink);
                }
            }
        }

        System.out.println("✅ Web results size: " + documents.size());

        return documents;
    }
}