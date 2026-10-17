package com.bookexchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String exchangeId;
    private String reviewerId;
    @Indexed
    private String reviewedUserId;
    private int rating;            // 1 to 5
    private String comment;
    private Instant createdAt;
}
