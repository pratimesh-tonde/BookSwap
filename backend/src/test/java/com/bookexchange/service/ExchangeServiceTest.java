package com.bookexchange.service;

import com.bookexchange.dto.ExchangeRequestDto;
import com.bookexchange.entity.Book;
import com.bookexchange.entity.Exchange;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.ExchangeRepository;
import com.bookexchange.repository.ReviewRepository;
import com.bookexchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExchangeServiceTest {

    @Mock ExchangeRepository exchangeRepository;
    @Mock BookRepository bookRepository;
    @Mock UserRepository userRepository;
    @Mock ReviewRepository reviewRepository;
    @Mock NotificationService notificationService;

    ExchangeService service;
    User alice;
    User bob;
    Book alicesBook;
    Book bobsBook;

    @BeforeEach
    void setUp() {
        service = new ExchangeService(exchangeRepository, bookRepository, userRepository,
                reviewRepository, notificationService);

        alice = user("u1", "Alice");
        bob = user("u2", "Bob");
        alicesBook = book("b1", "u1");
        bobsBook = book("b2", "u2");

        org.mockito.Mockito.when(bookRepository.findById("b1")).thenReturn(Optional.of(alicesBook));
        org.mockito.Mockito.when(bookRepository.findById("b2")).thenReturn(Optional.of(bobsBook));
        org.mockito.Mockito.when(exchangeRepository.save(org.mockito.ArgumentMatchers.any(Exchange.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void requestingOwnBookIsRejected() {
        // TC09: Alice tries to request her own book
        ApiException ex = assertThrows(ApiException.class,
                () -> service.create(alice, new ExchangeRequestDto("b1", "b1")));
        assertEquals("You cannot request your own book", ex.getMessage());
    }

    @Test
    void offeringSomeoneElsesBookIsRejected() {
        // Alice wants Bob's book but offers Bob's book as well
        assertThrows(ApiException.class,
                () -> service.create(alice, new ExchangeRequestDto("b2", "b2")));
    }

    @Test
    void duplicatePendingRequestIsRejected() {
        org.mockito.Mockito.when(exchangeRepository.existsByRequestedBookIdAndOfferedBookIdAndStatus("b2", "b1", "PENDING"))
                .thenReturn(true);
        assertThrows(ApiException.class,
                () -> service.create(alice, new ExchangeRequestDto("b2", "b1")));
    }

    @Test
    void acceptingMarksBothBooksExchanged() {
        // TC10: Bob accepts Alice's request
        Exchange request = new Exchange();
        request.setId("e1");
        request.setRequesterId("u1");
        request.setOwnerId("u2");
        request.setRequestedBookId("b2");
        request.setOfferedBookId("b1");
        request.setStatus("PENDING");
        org.mockito.Mockito.when(exchangeRepository.findById("e1")).thenReturn(Optional.of(request));

        service.accept(bob, "e1");

        assertEquals("ACCEPTED", request.getStatus());
        assertEquals("EXCHANGED", alicesBook.getStatus());
        assertEquals("EXCHANGED", bobsBook.getStatus());
    }

    @Test
    void onlyOwnerCanAccept() {
        Exchange request = new Exchange();
        request.setId("e1");
        request.setRequesterId("u1");
        request.setOwnerId("u2");
        request.setRequestedBookId("b2");
        request.setOfferedBookId("b1");
        request.setStatus("PENDING");
        org.mockito.Mockito.when(exchangeRepository.findById("e1")).thenReturn(Optional.of(request));

        assertThrows(ApiException.class, () -> service.accept(alice, "e1"));
    }

    private static User user(String id, String name) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        return u;
    }

    private static Book book(String id, String ownerId) {
        Book b = new Book();
        b.setId(id);
        b.setTitle("Book " + id);
        b.setOwnerId(ownerId);
        b.setStatus("AVAILABLE");
        return b;
    }
}
