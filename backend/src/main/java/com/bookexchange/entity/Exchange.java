package com.bookexchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "exchanges")
public class Exchange {
    @Id
    private String id;
    @Indexed
    private String requesterId;
    @Indexed
    private String ownerId;
    private String requestedBookId;  // the book the requester wants
    private String offeredBookId;    // the book the requester gives in return
    private String status = "PENDING"; // PENDING, ACCEPTED or REJECTED
    private Instant createdAt;
    private Instant updatedAt;
}
