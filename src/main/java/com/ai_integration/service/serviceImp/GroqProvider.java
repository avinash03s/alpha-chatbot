package com.ai_integration.service.serviceImp;

import com.ai_integration.service.AIProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroqProvider implements AIProvider {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${groq.api.key}")
    private String apiKey;

    @Override
    public String ask(String prompt) {

        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be empty");
        }

        try {

            Map<String, Object> body = Map.of(
                    "model", "llama3-8b-8192",
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    ),
                    "temperature", 0.7
            );

            String response = webClient.post()
                    .uri("https://api.groq.com/openai/v1/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from Groq API");
            }

            JsonNode json = mapper.readTree(response);

            if (json.has("error")) {
                throw new RuntimeException("Groq API Error: " + json.get("error").toString());
            }

            JsonNode choices = json.path("choices");

            if (!choices.isArray() || choices.size() == 0) {
                throw new RuntimeException("Invalid Groq response structure: " + response);
            }

            JsonNode messageNode = choices
                    .get(0)
                    .path("message")
                    .path("content");

            if (messageNode.isMissingNode() || messageNode.asText().isEmpty()) {
                throw new RuntimeException("Empty AI response content");
            }

            return messageNode.asText();

        } catch (Exception e) {
            throw new RuntimeException("Groq call failed: " + e.getMessage(), e);
        }
    }
}