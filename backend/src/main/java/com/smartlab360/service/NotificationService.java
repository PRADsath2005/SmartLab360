package com.smartlab360.backend.service;

import com.smartlab360.backend.entity.Notification;
import com.smartlab360.backend.entity.User;
import com.smartlab360.backend.repository.NotificationRepository;
import com.smartlab360.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification createNotification(
            Long userId,
            String message) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setReadStatus(false);

        return notificationRepository.save(
                notification
        );
    }

    public List<Notification> getUserNotifications(
            Long userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId
                );
    }

    public List<Notification> getUnreadNotifications(
            Long userId) {

        return notificationRepository
                .findByUserIdAndReadStatus(
                        userId,
                        false
                );
    }

    public long getUnreadCount(
            Long userId) {

        return notificationRepository
                .countByUserIdAndReadStatus(
                        userId,
                        false
                );
    }

    public Notification markAsRead(
            Long notificationId) {

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                ));

        notification.setReadStatus(true);

        return notificationRepository.save(
                notification
        );
    }

    public void markAllAsRead(
            Long userId) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdAndReadStatus(
                                userId,
                                false
                        );

        for (Notification notification :
                notifications) {

            notification.setReadStatus(true);
        }

        notificationRepository.saveAll(
                notifications
        );
    }

    public void deleteNotification(
            Long notificationId) {

        if (!notificationRepository
                .existsById(notificationId)) {

            throw new RuntimeException(
                    "Notification not found"
            );
        }

        notificationRepository.deleteById(
                notificationId
        );
    }
}