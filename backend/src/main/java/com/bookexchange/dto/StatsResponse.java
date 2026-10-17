package com.bookexchange.dto;

public record StatsResponse(long users, long books, long availableBooks, long exchangedBooks, long exchanges,
                            long pendingExchanges, long acceptedExchanges, long rejectedExchanges, long categories) {
}
