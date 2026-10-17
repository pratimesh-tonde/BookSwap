package com.bookexchange.controller;

import com.bookexchange.dto.BookResponse;
import com.bookexchange.entity.User;
import com.bookexchange.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public List<BookResponse> list(@AuthenticationPrincipal User user) {
        return wishlistService.list(user);
    }

    @PostMapping("/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@AuthenticationPrincipal User user, @PathVariable String bookId) {
        wishlistService.add(user, bookId);
    }

    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@AuthenticationPrincipal User user, @PathVariable String bookId) {
        wishlistService.remove(user, bookId);
    }
}
