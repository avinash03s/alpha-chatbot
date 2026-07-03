package com.ai_integration.dto;

public record ProviderResponse(
        String provider,
        String response,
        boolean success
) {
}