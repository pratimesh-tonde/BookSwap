package com.bookexchange.controller;

import com.bookexchange.dto.ExchangeRequestDto;
import com.bookexchange.dto.ExchangeResponse;
import com.bookexchange.entity.User;
import com.bookexchange.service.ExchangeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExchangeResponse create(@AuthenticationPrincipal User user, @Valid @RequestBody ExchangeRequestDto dto) {
        return exchangeService.create(user, dto);
    }

    @GetMapping("/sent")
    public List<ExchangeResponse> sent(@AuthenticationPrincipal User user) {
        return exchangeService.sent(user);
    }

    @GetMapping("/received")
    public List<ExchangeResponse> received(@AuthenticationPrincipal User user) {
        return exchangeService.received(user);
    }

    @GetMapping("/{id}")
    public ExchangeResponse get(@AuthenticationPrincipal User user, @PathVariable String id) {
        return exchangeService.get(user, id);
    }

    @PutMapping("/{id}/accept")
    public ExchangeResponse accept(@AuthenticationPrincipal User user, @PathVariable String id) {
        return exchangeService.accept(user, id);
    }

    @PutMapping("/{id}/reject")
    public ExchangeResponse reject(@AuthenticationPrincipal User user, @PathVariable String id) {
        return exchangeService.reject(user, id);
    }
}
