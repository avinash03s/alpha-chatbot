package com.ai_integration.provider;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAIProvider {

    // Injected OpenAI client (configured elsewhere via Spring bean)
    private final OpenAIClient client;

    public String ask(String prompt) {

        // Call the OpenAI Responses API with the given prompt
        Response response = client.responses().create(
                ResponseCreateParams.builder()
                        .model("gpt-5")           // model to use
                        .input(prompt)             // user's input prompt
                        .temperature(1.0)          // randomness of output (0 = deterministic, higher = more random)
                        .topP(1.0)                 // nucleus sampling parameter
                        .maxOutputTokens(4096L)     // max tokens allowed in the response
                        .build()
        );

        StringBuilder result = new StringBuilder();

        // Iterate over all output items returned by the API
        response.output().forEach(item -> {
            // Only process items that are actual messages (skip tool calls, etc.)
            item.message().ifPresent(message ->
                    // Each message can have multiple content blocks
                    message.content().forEach(content ->
                            // Only extract plain text content blocks
                            content.outputText().ifPresent(text ->
                                    result.append(text.text()) // append the text to our result
                            )
                    )
            );
        });

        // Return the combined text, trimmed of extra whitespace
        return result.toString().trim();
    }
}