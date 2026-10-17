package com.bookexchange.controller;

import com.bookexchange.dto.BookRequest;
import com.bookexchange.dto.BookResponse;
import com.bookexchange.dto.PageResponse;
import com.bookexchange.entity.User;
import com.bookexchange.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public PageResponse<BookResponse> list(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "12") int size) {
        return bookService.search(null, null, null, null, page, size);
    }

    @GetMapping("/search")
    public PageResponse<BookResponse> search(@RequestParam(required = false) String q,
                                             @RequestParam(required = false) String category,
                                             @RequestParam(required = false) String city,
                                             @RequestParam(required = false) String condition,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "12") int size) {
        return bookService.search(q, category, city, condition, page, size);
    }

    @GetMapping("/my")
    public List<BookResponse> mine(@AuthenticationPrincipal User user) {
        return bookService.myBooks(user);
    }

    @GetMapping("/{id}")
    public BookResponse get(@PathVariable String id) {
        return bookService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@AuthenticationPrincipal User user, @Valid @RequestBody BookRequest req) {
        return bookService.create(user, req);
    }

    @PutMapping("/{id}")
    public BookResponse update(@AuthenticationPrincipal User user, @PathVariable String id,
                               @Valid @RequestBody BookRequest req) {
        return bookService.update(user, id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable String id) {
        bookService.delete(user, id);
    }
}
