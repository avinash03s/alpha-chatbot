package com.ai_integration.service.serviceImp;

import com.ai_integration.dto.ProviderResponse;
import com.ai_integration.provider.*;
import com.ai_integration.service.GeminiService;
import com.ai_integration.service.ProviderHealthTracker;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiServiceImp implements GeminiService {

    private final GeminiProvider geminiProvider;
    private final GroqProvider groqProvider;
    private final OpenRouterProvider openRouterProvider;
    private final CohereProvider cohereProvider;
    private final MistralProvider mistralProvider;
    private final ProviderHealthTracker healthTracker;
    private final ExecutorService executorService;
    private final OpenAIProvider openAIProvider;

    // Cache
//    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final Cache<String, String> aiCache;

    @Override
    public String askGemini(String prompt) {

        // Cache
//        String cached = cache.get(prompt);
//
//        if (cached != null) {
//            log.info("Cache Hit");
//            return cached;
//        }
        String cached = aiCache.getIfPresent(prompt);

        if (cached != null) {

            log.info("Cache Hit");

            return cached;

        }

        String formattedPrompt = buildPrompt(prompt);

        CompletionService<ProviderResponse> completionService =
                new ExecutorCompletionService<>(executorService);

        List<Future<ProviderResponse>> futures = new ArrayList<>();

        int submittedTasks = 0;

        Map<String, Callable<String>> providers = getStringCallableMap(formattedPrompt);

        for (Map.Entry<String, Callable<String>> entry : providers.entrySet()) {

            String providerName = entry.getKey();

            if (!healthTracker.isAvailable(providerName)) {
                log.info("{} skipped (Daily Limit Reached)", providerName);
                continue;
            }

            Future<ProviderResponse> future = completionService.submit(() -> {

                long start = System.currentTimeMillis();

                try {

                    log.info("Calling {}", providerName);

                    String response = entry.getValue().call();

                    long time = System.currentTimeMillis() - start;

                    log.info("{} Success ({} ms)", providerName, time);

                    return new ProviderResponse(
                            providerName,
                            cleanResponse(response),
                            true
                    );

                } catch (Exception ex) {

                    long time = System.currentTimeMillis() - start;

                    log.warn("{} Failed after {} ms : {}",
                            providerName,
                            time,
                            ex.getMessage());

                    return new ProviderResponse(
                            providerName,
                            null,
                            false
                    );
                }

            });

            futures.add(future);
            submittedTasks++;
        }

        for (int i = 0; i < submittedTasks; i++) {

            try {

                Future<ProviderResponse> future = completionService.take();

                ProviderResponse result = future.get();

                if (result.success()) {

                    healthTracker.record(result.provider());

//                    cache.put(prompt, result.response());
                    aiCache.put(prompt, result.response());

                    cancelRemaining(futures);

                    return result.response();
                }

            } catch (Exception e) {

                log.error("Execution Error : {}", e.getMessage());

            }

        }

        return "All AI services are currently unavailable.";
    }

    private void cancelRemaining(List<Future<ProviderResponse>> futures) {

        for (Future<ProviderResponse> future : futures) {

            if (!future.isDone()) {
                future.cancel(true);
            }

        }

    }

    private Map<String, Callable<String>> getStringCallableMap(String prompt) {

        Map<String, Callable<String>> providers = new LinkedHashMap<>();

        providers.put("OpenAI", () -> openAIProvider.ask(prompt));

        providers.put("Groq", () -> groqProvider.ask(prompt));

//    providers.put("Gemini", () -> geminiProvider.ask(prompt));

        providers.put("Mistral", () -> mistralProvider.ask(prompt));

        providers.put("OpenRouter", () -> openRouterProvider.ask(prompt));

        providers.put("Cohere", () -> cohereProvider.ask(prompt));

        return providers;
    }

    //    private String buildPrompt(String userPrompt) {
//
//        return """
//            You are Alpha, an intelligent AI assistant created by Avinash.
//
//            Identity Rules:
//            - Your name is Alpha.
//            - Never say your name is ChatGPT, Gemini, Grok, Claude, or Mistral.
//            - If someone asks who created you, answer: Avinash.
//            - Be friendly and professional.
//
//            Response Rules:
//            - Use bullet points when appropriate.
//            - Keep answers concise.
//            - No markdown.
//            - Avoid unnecessary long explanations.
//
//            User Question:
//            """ + userPrompt;
//    }
    private String buildPrompt(String userPrompt) {

        return """
                You are Alpha, an AI assistant created by Avinash.
                
                Identity:
                - Your name is Alpha.
                - Introduce yourself as Alpha when asked.
                - If asked who created you, answer: Avinash.
                
                Behavior:
                - Be friendly, professional, and accurate.
                - Adapt the length of your answer to the user's request.
                - Use Markdown formatting when it improves readability.
                - Use bullet points, tables, or code blocks when appropriate.
                - If the user asks for code, provide complete, runnable examples with explanations.
                - If you're unsure about something, say so instead of guessing.
                - Keep explanations clear and well-structured.
                
                User Question:
                """ + userPrompt;

    }

    private String cleanResponse(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("**", "")
                .replace("###", "")
                .replace("```", "")
                .replace("\n\n", "\n")
                .trim();
    }
}


//--------------------------------------------------------------------------------------

//package com.ai_integration.service.serviceImp;
//
//import com.ai_integration.provider.*;
//import com.ai_integration.service.GeminiService;
//import com.ai_integration.service.ProviderHealthTracker;
//import com.google.genai.Chat;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.concurrent.*;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class GeminiServiceImp implements GeminiService {
//
//    private final GeminiProvider geminiProvider;
//    private final GroqProvider groqProvider;
//    private final OpenRouterProvider openRouterProvider;
//    private final CohereProvider cohereProvider;
//    private final MistralProvider mistralProvider;
//    private final ProviderHealthTracker healthTracker;
//
//    private final ExecutorService executorService;
//
//    // Cache — same question = instant reply
//    private final Map<String, String> cache = new ConcurrentHashMap<>();
//
//    @Override
//    public String askGemini(String prompt) {
//
//        // Return instantly if same question asked before
//        if (cache.containsKey(prompt)) {
//            log.info("Cache hit for prompt");
//            return cache.get(prompt);
//        }
//
//        Map<String, Callable<String>> providers = getStringCallableMap(prompt);
//
//        for (var entry : providers.entrySet()) {
//            String name = entry.getKey();
//
//            // Skip if daily limit reached — auto resets next day
//            if (!healthTracker.isAvailable(name)) {
//                log.info("Skipping {} — daily limit reached", name);
//                continue;
//            }
//
//
//            try {
//                log.info("Trying {}...", name);
//                String result =
//                        executorService
//                                .submit(entry.getValue())
//                                .get(3, TimeUnit.SECONDS);
//                healthTracker.record(name);
//
/// /                String cleaned = cleanResponse(result);
/// /                cache.put(prompt, cleaned); // save to cache
/// /                return cleaned;
//
//            } catch (TimeoutException e) {
//                log.warn("{} timed out after 8s", name);
//            } catch (Exception e) {
//                log.warn("{} failed: {}", name, e.getMessage());
//            }
////            } finally {
////                ex.shutdownNow();
////            }
//        }
//
//        return "All AI services are currently unavailable.";
//    }
//
//    private Map<String, Callable<String>> getStringCallableMap(String prompt) {
//        String fp = buildPrompt(prompt);
//
//        // Groq first = fastest (1-3 sec), others as fallback
//        Map<String, Callable<String>> providers = new LinkedHashMap<>();
//        providers.put("Groq",       () -> groqProvider.ask(fp));
////        providers.put("Gemini",     () -> geminiProvider.ask(fp));
//        providers.put("Mistral",    () -> mistralProvider.ask(fp));
//        providers.put("OpenRouter", () -> openRouterProvider.ask(fp));
//        providers.put("Cohere",     () -> cohereProvider.ask(fp));
//        return providers;
//    }
//
//    private String buildPrompt(String userPrompt) {
//        return """
//        You are Alpha, an intelligent AI assistant created by Avinash.
//
//        Identity Rules:
//        - Your name is Alpha.
//        - When someone asks your name, reply that your name is Alpha.
//        - Never say your name is ChatGPT, Gemini, Grok, Claude, Mistral, or any other AI.
//        - If asked who created you, answer: Avinash.
//        - Be friendly and professional.
//
//        Response Rules:
//        - Use bullet points when appropriate.
//        - Keep answers concise.
//        - No markdown.
//        - Avoid unnecessarily long explanations.
//
//        User Question:
//        """ + userPrompt;
//    }
//
//    private String cleanResponse(String text) {
//        if (text == null) return "";
//        return text
//                .replace("**", "")
//                .replace("###", "")
//                .replace("```", "")
//                .replace("\n\n", "\n")
//                .trim();
//    }
//}

//--------------------------------------------------------------------------------------------------

//package com.ai_integration.service.serviceImp;
//
//import com.ai_integration.provider.*;
//import com.ai_integration.service.GeminiService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class GeminiServiceImp implements GeminiService {
//    private final GeminiProvider geminiProvider;
//    private final GroqProvider groqProvider;
//    private final OpenRouterProvider openRouterProvider;
//    private final CohereProvider cohereProvider;
//    private final MistralProvider mistralProvider;
//
//    @Override
//    public String askGemini(String prompt) {
//
//        String formattedPrompt = buildPrompt(prompt);
//        for (int i = 0; i < 5; i++) {
//            // GEMINI
//            try {
//
//                log.info("Trying Gemini...");
//                return cleanResponse(geminiProvider.ask(formattedPrompt));
//
//            } catch (Exception e) {
//
//                log.error("Gemini Failed: {}", e.getMessage());
//
//                // GROQ
//                try {
//
//                    log.info("Trying Groq...");
//
//                    return cleanResponse(groqProvider.ask(formattedPrompt));
//                } catch (Exception ex) {
//
//                    log.error("Groq Failed: {}", ex.getMessage());
//
//                    // OPENROUTER
//                    try {
//                        log.info("Trying OpenRouter...");
//                        return cleanResponse(openRouterProvider.ask(formattedPrompt));
//                    } catch (Exception exc) {
//                        log.error("OpenRouter Failed: {}", exc.getMessage());
//
//                        //Cohere
//                        try {
//                            log.info("Trying Cohere");
//                            return cleanResponse(cohereProvider.ask(formattedPrompt));
//                        } catch (Exception e1) {
//                            log.error("Cohere Failed: {}",e1.getMessage());
//
//                            //Mistral
//                            try {
//                                log.info("Trying Mistral..");
//                                return cleanResponse(mistralProvider.ask(formattedPrompt));
//                            } catch (Exception e2) {
//                                log.error("Mist Failed: {}",e2.getMessage());
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return "All AI services are currently unavailable.";
//    }
//
//    private String buildPrompt(String userPrompt) {
//
//        return """
//            You are a medical AI assistant.
//
//            Rules:
//            - Use bullet points
//            - Keep answers short
//            - No markdown
//            - Avoid long explanations
//
//            User Question:
//            """ + userPrompt;
//    }
//
//    private String cleanResponse(String text) {
//
//        if (text == null) return "";
//
//        return text
//                .replace("**", "")
//                .replace("###", "")
//                .replace("```", "")
//                .replace("\n\n", "\n")
//                .trim();
//    }
//}