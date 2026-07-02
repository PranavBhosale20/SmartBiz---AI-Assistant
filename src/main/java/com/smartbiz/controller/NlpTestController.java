package com.smartbiz.controller;

import com.smartbiz.nlp.IntentResult;
import com.smartbiz.nlp.NlpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TEMPORARY - Phase 7 testing only.
 * Lets us verify the NLP engine via Postman without
 * building the full /chat endpoint yet (that's Phase 9).
 * This controller will be replaced by ChatController in Phase 9.
 */
@RestController
@RequestMapping("/api/nlp-test")
public class NlpTestController {

    private final NlpService nlpService;

    public NlpTestController(NlpService nlpService) {
        this.nlpService = nlpService;
    }

    @PostMapping
    public ResponseEntity<IntentResult> test(@RequestBody MessageRequest request) {
        IntentResult result = nlpService.process(request.getMessage());
        return ResponseEntity.ok(result);
    }

    // Simple inner class - no need for a separate DTO file
    // for a temporary test endpoint
    public static class MessageRequest {
        private String message;
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}