package com.bookexchange.controller;

import com.bookexchange.entity.Notification;
import com.bookexchange.entity.User;
import com.bookexchange.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> list(@AuthenticationPrincipal User user) {
        return notificationService.list(user);
    }

    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void readAll(@AuthenticationPrincipal User user) {
        notificationService.markAllRead(user);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void read(@AuthenticationPrincipal User user, @PathVariable String id) {
        notificationService.markRead(user, id);
    }
}
