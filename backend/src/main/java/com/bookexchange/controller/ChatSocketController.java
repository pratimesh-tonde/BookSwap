package com.bookexchange.controller;

import com.bookexchange.dto.ChatPayload;
import com.bookexchange.entity.ChatMessage;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/** Receives chat messages sent to /app/chat/{exchangeId}, stores them and broadcasts to /topic/exchange/{exchangeId}. */
@Controller
public class ChatSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{exchangeId}")
    public void send(@DestinationVariable String exchangeId, @Payload ChatPayload payload, Principal principal) {
        if (!(principal instanceof Authentication auth) || !(auth.getPrincipal() instanceof User user)) {
            throw ApiException.unauthorized("Not logged in");
        }
        ChatMessage saved = chatService.save(user, exchangeId, payload.text());
        messagingTemplate.convertAndSend("/topic/exchange/" + exchangeId, saved);
    }
}
