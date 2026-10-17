package com.bookexchange.service;

import com.bookexchange.dto.BookResponse;
import com.bookexchange.entity.Book;
import com.bookexchange.entity.User;
import com.bookexchange.entity.WishlistItem;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    public WishlistService(WishlistRepository wishlistRepository, BookRepository bookRepository, BookService bookService) {
        this.wishlistRepository = wishlistRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    public List<BookResponse> list(User user) {
        List<String> bookIds = wishlistRepository.findByUserId(user.getId()).stream().map(WishlistItem::getBookId).toList();
        List<Book> books = bookRepository.findAllById(bookIds);
        return bookService.toResponses(books);
    }

    public void add(User user, String bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw ApiException.notFound("Book not found");
        }
        if (!wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            WishlistItem item = new WishlistItem();
            item.setUserId(user.getId());
            item.setBookId(bookId);
            wishlistRepository.save(item);
        }
    }

    public void remove(User user, String bookId) {
        wishlistRepository.deleteByUserIdAndBookId(user.getId(), bookId);
    }
}
