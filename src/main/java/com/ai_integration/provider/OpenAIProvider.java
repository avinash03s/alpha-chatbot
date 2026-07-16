package com.ai_integration.provider;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAIProvider {

    private final OpenAIClient client;

    public String ask(String prompt) {

        Response response = client.responses().create(
                ResponseCreateParams.builder()
                        .model("gpt-5")
                        .input(prompt)
                        .temperature(1.0)
                        .topP(1.0)
                        .maxOutputTokens(4096)
                        .build()
        );

        return response.outputText();
    }
}