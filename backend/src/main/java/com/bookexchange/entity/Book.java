package com.bookexchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "books")
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private String categoryId;
    private String condition;      // New, Like New, Good, Fair
    private String description;
    private String imageUrl;
    @Indexed
    private String ownerId;
    private String city;           // copied from the owner so books can be filtered by city
    private String status = "AVAILABLE"; // AVAILABLE or EXCHANGED
    private Instant createdAt;
}