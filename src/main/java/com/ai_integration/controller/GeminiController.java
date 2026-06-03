package com.ai_integration.controller;

import com.ai_integration.dto.AIRequest;
import com.ai_integration.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/ask")
    public ResponseEntity<String> askGemini(@RequestBody AIRequest request){
        String response = geminiService.askGemini(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}