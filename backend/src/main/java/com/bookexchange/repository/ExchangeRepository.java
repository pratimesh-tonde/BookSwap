package com.bookexchange.repository;

import com.bookexchange.entity.Exchange;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ExchangeRepository extends MongoRepository<Exchange, String> {
    List<Exchange> findByRequesterIdOrderByCreatedAtDesc(String requesterId);
    List<Exchange> findByOwnerIdOrderByCreatedAtDesc(String ownerId);
    boolean existsByRequestedBookIdAndOfferedBookIdAndStatus(String requestedBookId, String offeredBookId, String status);
    List<Exchange> findByStatusAndRequestedBookIdIn(String status, Collection<String> bookIds);
    List<Exchange> findByStatusAndOfferedBookIdIn(String status, Collection<String> bookIds);
    long countByStatus(String status);
}
