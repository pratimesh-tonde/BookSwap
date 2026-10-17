package com.bookexchange.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Locale;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    @SuppressWarnings("unused")
    @JsonIgnore
    private String password;      // BCrypt hash
    private String phone;
    private String city;
    private String role = "USER"; // USER or ADMIN
    private boolean blocked;
    private double avgRating;
    private int ratingCount;

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
