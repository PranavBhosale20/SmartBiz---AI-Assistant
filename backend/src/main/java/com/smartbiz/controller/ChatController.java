package com.smartbiz.controller;

import com.smartbiz.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Phase 9: unified chat endpoint.
 * Replaces the temporary NlpTestController from Phase 7.
 * Accepts a natural language message, returns a structured
 * response with the detected intent + executed result.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.process(request.getMessage());
        return ResponseEntity.ok(response);
    }
}