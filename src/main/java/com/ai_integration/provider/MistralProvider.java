//package com.ai_integration.provider;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class MistralProvider implements AIProvider {
//
//    @Value("${mistral.api.key}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate = buildRestTemplate();
//    private static final String URL = "https://api.mistral.ai/v1/chat/completions";
//
//    @Override
//    public String ask(String prompt){
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey);
//
//        Map<String, Object> body = Map.of(
//                "model", "mistral-small-latest",  // free tier model
//                "messages", List.of(Map.of("role", "user", "content", prompt)),
//                "max_tokens", 1024
//        );
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//        ResponseEntity<Map> response = restTemplate.postForEntity(URL, request, Map.class);
//
//        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
//        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
//        return (String) message.get("content");
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
public class MistralProvider implements AIProvider {

    private final WebClient webClient;

    @Value("${mistral.api.key}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String ask(String prompt) {

        Map<String, Object> body = Map.of(
                "model", "mistral-small-latest",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 1024
        );

        String response = webClient.post()
                .uri("https://api.mistral.ai/v1/chat/completions")
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
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Mistral parsing failed", e);
        }
    }
}