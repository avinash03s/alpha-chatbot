package com.ai_integration.provider;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.HttpOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class GeminiProvider implements AIProvider {

    private final Client client;

    @Override
    public String ask(String prompt) {
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                GenerateContentConfig.builder()
                        .httpOptions(HttpOptions.builder()
                                .timeout(3000)
                                .build())
                        .build()
        );
        return response.text();
    }
}