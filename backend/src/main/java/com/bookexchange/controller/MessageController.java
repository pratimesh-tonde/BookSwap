package com.bookexchange.controller;

import com.bookexchange.entity.ChatMessage;
import com.bookexchange.entity.User;
import com.bookexchange.service.ChatService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST endpoint to load the chat history; new messages travel over the WebSocket. */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final ChatService chatService;

    public MessageController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{exchangeId}")
    public List<ChatMessage> history(@AuthenticationPrincipal User user, @PathVariable String exchangeId) {
        return chatService.history(user, exchangeId);
    }
}
