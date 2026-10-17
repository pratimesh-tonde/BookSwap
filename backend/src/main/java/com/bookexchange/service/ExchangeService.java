package com.bookexchange.service;

import com.bookexchange.dto.ExchangeRequestDto;
import com.bookexchange.dto.ExchangeResponse;
import com.bookexchange.entity.Book;
import com.bookexchange.entity.Exchange;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.ExchangeRepository;
import com.bookexchange.repository.ReviewRepository;
import com.bookexchange.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** The core business logic: request -> accept / reject. */
@Service
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationService notificationService;

    public ExchangeService(ExchangeRepository exchangeRepository, BookRepository bookRepository,
                           UserRepository userRepository, ReviewRepository reviewRepository,
                           NotificationService notificationService) {
        this.exchangeRepository = exchangeRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.notificationService = notificationService;
    }

    public ExchangeResponse create(User user, ExchangeRequestDto dto) {
        Book requested = bookRepository.findById(dto.requestedBookId())
                .orElseThrow(() -> ApiException.notFound("The book you want was not found"));
        Book offered = bookRepository.findById(dto.offeredBookId())
                .orElseThrow(() -> ApiException.notFound("The book you offered was not found"));

        if (requested.getOwnerId().equals(user.getId())) {
            throw ApiException.badRequest("You cannot request your own book");
        }
        if (!offered.getOwnerId().equals(user.getId())) {
            throw ApiException.badRequest("You can offer only a book that belongs to you");
        }
        if (!"AVAILABLE".equals(requested.getStatus()) || !"AVAILABLE".equals(offered.getStatus())) {
            throw ApiException.badRequest("Books that are already exchanged cannot be requested or offered");
        }
        if (exchangeRepository.existsByRequestedBookIdAndOfferedBookIdAndStatus(
                requested.getId(), offered.getId(), "PENDING")) {
            throw ApiException.conflict("You already sent a request for this pair of books");
        }

        Exchange ex = new Exchange();
        ex.setRequesterId(user.getId());
        ex.setOwnerId(requested.getOwnerId());
        ex.setRequestedBookId(requested.getId());
        ex.setOfferedBookId(offered.getId());
        ex.setStatus("PENDING");
        ex.setCreatedAt(Instant.now());
        ex.setUpdatedAt(Instant.now());
        exchangeRepository.save(ex);

        notificationService.notify(requested.getOwnerId(),
                user.getName() + " wants to exchange \"" + offered.getTitle() + "\" for your book \""
                        + requested.getTitle() + "\".");
        return toResponses(List.of(ex), user.getId()).get(0);
    }

    public ExchangeResponse accept(User user, String id) {
        Exchange ex = find(id);
        if (!ex.getOwnerId().equals(user.getId())) {
            throw ApiException.forbidden("Only the book owner can accept this request");
        }
        if (!"PENDING".equals(ex.getStatus())) {
            throw ApiException.conflict("This request is already " + ex.getStatus().toLowerCase());
        }
        Book requested = bookRepository.findById(ex.getRequestedBookId())
                .orElseThrow(() -> ApiException.conflict("The requested book no longer exists"));
        Book offered = bookRepository.findById(ex.getOfferedBookId())
                .orElseThrow(() -> ApiException.conflict("The offered book no longer exists"));
        if (!"AVAILABLE".equals(requested.getStatus()) || !"AVAILABLE".equals(offered.getStatus())) {
            throw ApiException.conflict("One of the books is no longer available");
        }

        requested.setStatus("EXCHANGED");
        offered.setStatus("EXCHANGED");
        bookRepository.saveAll(List.of(requested, offered));

        ex.setStatus("ACCEPTED");
        ex.setUpdatedAt(Instant.now());
        exchangeRepository.save(ex);

        // every other pending request that involves either of the two books can no longer be fulfilled
        List<String> bookIds = List.of(requested.getId(), offered.getId());
        Map<String, Exchange> others = new LinkedHashMap<>();
        exchangeRepository.findByStatusAndRequestedBookIdIn("PENDING", bookIds).forEach(o -> others.put(o.getId(), o));
        exchangeRepository.findByStatusAndOfferedBookIdIn("PENDING", bookIds).forEach(o -> others.put(o.getId(), o));
        others.remove(ex.getId());
        for (Exchange other : others.values()) {
            other.setStatus("REJECTED");
            other.setUpdatedAt(Instant.now());
            exchangeRepository.save(other);
            notificationService.notify(other.getRequesterId(),
                    "Your exchange request was declined because the book is no longer available.");
        }

        notificationService.notify(ex.getRequesterId(),
                user.getName() + " accepted your request for \"" + requested.getTitle()
                        + "\". Open the chat to arrange the handover.");
        return toResponses(List.of(ex), user.getId()).get(0);
    }

    public ExchangeResponse reject(User user, String id) {
        Exchange ex = find(id);
        if (!ex.getOwnerId().equals(user.getId())) {
            throw ApiException.forbidden("Only the book owner can reject this request");
        }
        if (!"PENDING".equals(ex.getStatus())) {
            throw ApiException.conflict("This request is already " + ex.getStatus().toLowerCase());
        }
        ex.setStatus("REJECTED");
        ex.setUpdatedAt(Instant.now());
        exchangeRepository.save(ex);
        notificationService.notify(ex.getRequesterId(), user.getName() + " declined your exchange request.");
        return toResponses(List.of(ex), user.getId()).get(0);
    }

    public List<ExchangeResponse> sent(User user) {
        return toResponses(exchangeRepository.findByRequesterIdOrderByCreatedAtDesc(user.getId()), user.getId());
    }

    public List<ExchangeResponse> received(User user) {
        return toResponses(exchangeRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId()), user.getId());
    }

    public ExchangeResponse get(User user, String id) {
        Exchange ex = find(id);
        assertParticipant(ex, user.getId());
        return toResponses(List.of(ex), user.getId()).get(0);
    }

    public Exchange getForParticipant(User user, String id) {
        Exchange ex = find(id);
        assertParticipant(ex, user.getId());
        return ex;
    }

    private Exchange find(String id) {
        return exchangeRepository.findById(id).orElseThrow(() -> ApiException.notFound("Exchange request not found"));
    }

    private void assertParticipant(Exchange ex, String userId) {
        if (!ex.getRequesterId().equals(userId) && !ex.getOwnerId().equals(userId)) {
            throw ApiException.forbidden("You are not part of this exchange");
        }
    }

    /** Adds book titles, images and user names using batched look-ups. */
    List<ExchangeResponse> toResponses(List<Exchange> exchanges, String viewerId) {
        Set<String> bookIds = new java.util.HashSet<>();
        Set<String> userIds = new java.util.HashSet<>();
        for (Exchange e : exchanges) {
            bookIds.add(e.getRequestedBookId());
            bookIds.add(e.getOfferedBookId());
            userIds.add(e.getRequesterId());
            userIds.add(e.getOwnerId());
        }
        Map<String, Book> books = bookRepository.findAllById(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));
        Map<String, User> users = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<ExchangeResponse> result = new ArrayList<>();
        for (Exchange e : exchanges) {
            Book requested = books.get(e.getRequestedBookId());
            Book offered = books.get(e.getOfferedBookId());
            User requester = users.get(e.getRequesterId());
            User owner = users.get(e.getOwnerId());
            boolean reviewed = "ACCEPTED".equals(e.getStatus())
                    && reviewRepository.existsByExchangeIdAndReviewerId(e.getId(), viewerId);
            result.add(new ExchangeResponse(
                    e.getId(), e.getStatus(), e.getCreatedAt(), e.getUpdatedAt(),
                    e.getRequesterId(), requester == null ? "Unknown user" : requester.getName(),
                    e.getOwnerId(), owner == null ? "Unknown user" : owner.getName(),
                    e.getRequestedBookId(), requested == null ? "Removed book" : requested.getTitle(),
                    requested == null ? null : requested.getImageUrl(),
                    e.getOfferedBookId(), offered == null ? "Removed book" : offered.getTitle(),
                    offered == null ? null : offered.getImageUrl(),
                    reviewed));
        }
        return result;
    }
}
