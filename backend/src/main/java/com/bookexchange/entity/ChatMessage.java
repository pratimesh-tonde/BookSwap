package com.bookexchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "messages")
public class ChatMessage {
    @Id
    private String id;
    @Indexed
    private String exchangeId;
    private String senderId;
    private String text;
    private Instant sentAt;
}
