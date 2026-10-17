package com.bookexchange.service;

import com.bookexchange.entity.Notification;
import com.bookexchange.entity.User;
import com.bookexchange.exception.ApiException;
import com.bookexchange.repository.NotificationRepository;
import com.bookexchange.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/** Creates an in-app notification and also emails the user. */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void notify(String userId, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        n.setRead(false);
        n.setCreatedAt(Instant.now());
        notificationRepository.save(n);
        userRepository.findById(userId)
                .ifPresent(u -> emailService.send(u.getEmail(), "Book Exchange Portal", message));
    }

    public List<Notification> list(User user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public void markRead(User user, String id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Notification not found"));
        if (!n.getUserId().equals(user.getId())) {
            throw ApiException.forbidden("Not your notification");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    public void markAllRead(User user) {
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().filter(n -> !n.isRead()).toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
