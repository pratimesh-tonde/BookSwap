package com.bookexchange.service;

import com.bookexchange.dto.BookRequest;
import com.bookexchange.dto.BookResponse;
import com.bookexchange.dto.PageResponse;
import com.bookexchange.entity.Book;
import com.bookexchange.entity.Category;
import com.bookexchange.entity.Exchange;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.CategoryRepository;
import com.bookexchange.repository.ExchangeRepository;
import com.bookexchange.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExchangeRepository exchangeRepository;
    private final NotificationService notificationService;
    private final MongoTemplate mongoTemplate;

    public BookService(BookRepository bookRepository, UserRepository userRepository,
                       CategoryRepository categoryRepository, ExchangeRepository exchangeRepository,
                       NotificationService notificationService, MongoTemplate mongoTemplate) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.exchangeRepository = exchangeRepository;
        this.notificationService = notificationService;
        this.mongoTemplate = mongoTemplate;
    }

    /** Search + filter (title/author keyword, category, city, condition). Only AVAILABLE books, newest first. */
    public PageResponse<BookResponse> search(String q, String categoryId, String city, String condition,
                                             int page, int size) {
        List<Criteria> filters = new ArrayList<>();
        filters.add(Criteria.where("status").is("AVAILABLE"));
        if (StringUtils.hasText(q)) {
            String regex = Pattern.quote(q.trim());
            filters.add(new Criteria().orOperator(
                    Criteria.where("title").regex(regex, "i"),
                    Criteria.where("author").regex(regex, "i")));
        }
        if (StringUtils.hasText(categoryId)) {
            filters.add(Criteria.where("categoryId").is(categoryId));
        }
        if (StringUtils.hasText(city)) {
            filters.add(Criteria.where("city").regex(Pattern.quote(city.trim()), "i"));
        }
        if (StringUtils.hasText(condition)) {
            filters.add(Criteria.where("condition").is(condition));
        }

        Query query = new Query(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        long total = mongoTemplate.count(query, Book.class);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Book> books = mongoTemplate.find(query.with(pageable), Book.class);
        return PageResponse.of(toResponses(books), safePage, safeSize, total);
    }

    public BookResponse get(String id) {
        return toResponses(List.of(find(id))).get(0);
    }

    public List<BookResponse> myBooks(User user) {
        return toResponses(bookRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId()));
    }

    public BookResponse create(User user, BookRequest req) {
        assertCategoryExists(req.categoryId());
        Book book = new Book();
        apply(book, req);
        book.setOwnerId(user.getId());
        book.setCity(user.getCity());
        book.setStatus("AVAILABLE");
        book.setCreatedAt(Instant.now());
        bookRepository.save(book);
        return toResponses(List.of(book)).get(0);
    }

    public BookResponse update(User user, String id, BookRequest req) {
        Book book = find(id);
        if (!book.getOwnerId().equals(user.getId())) {
            throw ApiException.forbidden("You can edit only your own books");
        }
        if ("EXCHANGED".equals(book.getStatus())) {
            throw ApiException.badRequest("An exchanged book cannot be edited");
        }
        assertCategoryExists(req.categoryId());
        apply(book, req);
        bookRepository.save(book);
        return toResponses(List.of(book)).get(0);
    }

    public void delete(User user, String id) {
        Book book = find(id);
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!isAdmin && !book.getOwnerId().equals(user.getId())) {
            throw ApiException.forbidden("You can delete only your own books");
        }
        cancelPendingRequests(book);
        bookRepository.delete(book);
    }

    /** Requests that involve a book being removed can never be completed, so they are rejected. */
    private void cancelPendingRequests(Book book) {
        List<Exchange> pending = new ArrayList<>();
        pending.addAll(exchangeRepository.findByStatusAndRequestedBookIdIn("PENDING", List.of(book.getId())));
        pending.addAll(exchangeRepository.findByStatusAndOfferedBookIdIn("PENDING", List.of(book.getId())));
        for (Exchange ex : pending) {
            ex.setStatus("REJECTED");
            ex.setUpdatedAt(Instant.now());
            exchangeRepository.save(ex);
            notificationService.notify(ex.getRequesterId(),
                    "Your exchange request was cancelled because \"" + book.getTitle() + "\" was removed.");
        }
    }

    /** Admin: every book (available and exchanged). */
    public PageResponse<BookResponse> adminList(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        var result = bookRepository.findAll(PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageResponse.of(toResponses(result.getContent()), safePage, safeSize, result.getTotalElements());
    }

    /** Adds owner and category details to books using two batched look-ups (no N+1 queries). */
    public List<BookResponse> toResponses(List<Book> books) {
        Set<String> ownerIds = books.stream().map(Book::getOwnerId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> categoryIds = books.stream().map(Book::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, User> owners = userRepository.findAllById(ownerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<String, Category> categories = categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
        return books.stream()
                .map(b -> BookResponse.from(b, owners.get(b.getOwnerId()), categories.get(b.getCategoryId())))
                .toList();
    }

    private Book find(String id) {
        return bookRepository.findById(id).orElseThrow(() -> ApiException.notFound("Book not found"));
    }

    private void assertCategoryExists(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw ApiException.badRequest("Selected category does not exist");
        }
    }

    private void apply(Book book, BookRequest req) {
        book.setTitle(req.title().trim());
        book.setAuthor(req.author().trim());
        book.setCategoryId(req.categoryId());
        book.setCondition(req.condition());
        book.setDescription(req.description());
        book.setImageUrl(req.imageUrl());
    }
}
