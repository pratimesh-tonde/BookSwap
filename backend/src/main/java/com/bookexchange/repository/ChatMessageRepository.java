package com.bookexchange.repository;

import com.bookexchange.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByExchangeIdOrderBySentAtAsc(String exchangeId);
}
