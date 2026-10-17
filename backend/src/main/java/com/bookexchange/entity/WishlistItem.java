package com.bookexchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "wishlist")
@CompoundIndex(name = "user_book_unique", def = "{'userId': 1, 'bookId': 1}", unique = true)
public class WishlistItem {
    @Id
    private String id;
    private String userId;
    private String bookId;
}
