//package com.ai_integration.provider;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Map;
//
//@Component
//public class CohereProvider implements AIProvider {
//
//    @Value("${cohere.api.key}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate = buildRestTemplate();
//    private static final String URL = "https://api.cohere.com/v2/chat";
//
//    @Override
//    public String ask(String prompt){
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey);
//
//        Map<String, Object> body = Map.of(
//                "model", "command-r",   // free tier model
//                "messages", java.util.List.of(Map.of("role", "user", "content", prompt))
//        );
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//        ResponseEntity<Map> response = restTemplate.postForEntity(URL, request, Map.class);
//
//        Map<String, Object> message = (Map<String, Object>) response.getBody().get("message");
//        java.util.List<Map<String, Object>> content = (java.util.List<Map<String, Object>>) message.get("content");
//        return (String) content.get(0).get("text");
//    }
//
//    private RestTemplate buildRestTemplate() {
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setConnectTimeout(3000);
//        factory.setReadTimeout(3000);
//        return new RestTemplate(factory);
//    }
//}


package com.ai_integration.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CohereProvider implements AIProvider {

    private final WebClient webClient;

    @Value("${cohere.api.key}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String ask(String prompt) {

        Map<String, Object> body = Map.of(
                "model", "command-r",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        String response = webClient.post()
                .uri("https://api.cohere.com/v2/chat")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        try {

            JsonNode json = mapper.readTree(response);

            return json
                    .get("message")
                    .get("content")
                    .get(0)
                    .get("text")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Cohere parsing failed", e);
        }
    }
}