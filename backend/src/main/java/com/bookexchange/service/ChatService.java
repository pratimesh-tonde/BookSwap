package com.bookexchange.service;

import com.bookexchange.entity.ChatMessage;
import com.bookexchange.entity.Exchange;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ExchangeService exchangeService;

    public ChatService(ChatMessageRepository chatMessageRepository, ExchangeService exchangeService) {
        this.chatMessageRepository = chatMessageRepository;
        this.exchangeService = exchangeService;
    }

    public List<ChatMessage> history(User user, String exchangeId) {
        exchangeService.getForParticipant(user, exchangeId);
        return chatMessageRepository.findByExchangeIdOrderBySentAtAsc(exchangeId);
    }

    public ChatMessage save(User sender, String exchangeId, String text) {
        Exchange ex = exchangeService.getForParticipant(sender, exchangeId);
        if ("REJECTED".equals(ex.getStatus())) {
            throw ApiException.badRequest("Chat is closed because this request was rejected");
        }
        String clean = text == null ? "" : text.trim();
        if (clean.isEmpty()) {
            throw ApiException.badRequest("Message cannot be empty");
        }
        if (clean.length() > 1000) {
            clean = clean.substring(0, 1000);
        }
        ChatMessage m = new ChatMessage();
        m.setExchangeId(exchangeId);
        m.setSenderId(sender.getId());
        m.setText(clean);
        m.setSentAt(Instant.now());
        return chatMessageRepository.save(m);
    }
}
