package com.ai_integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ProviderHealthTracker {

    private final Map<String, Integer> counts = new ConcurrentHashMap<>();
    private final Map<String, LocalDate> dates = new ConcurrentHashMap<>();

    // Free tier daily limits
    private static final Map<String, Integer> LIMITS = Map.of(
            "Groq",       6000,
            "Gemini",     1500,
            "Mistral",    1000,
            "OpenRouter", 200,
            "Cohere",     40
    );

    public boolean isAvailable(String name) {
        // New day? auto reset count
        if (!LocalDate.now().equals(dates.get(name))) {
            counts.put(name, 0);
            dates.put(name, LocalDate.now());
            log.info("{} daily count reset", name);
        }
        int used = counts.getOrDefault(name, 0);
        int limit = LIMITS.getOrDefault(name, 9999);
        return used < limit;
    }

    public void record(String name) {
        counts.merge(name, 1, Integer::sum);
        log.info("{} used {}/{}", name, counts.get(name), LIMITS.getOrDefault(name, 9999));
    }
}