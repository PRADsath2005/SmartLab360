package com.smartlab360.backend.controller;

import com.smartlab360.backend.entity.Notification;
import com.smartlab360.backend.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // Create notification
    @PostMapping
    public ResponseEntity<?> createNotification(
            @RequestParam Long userId,
            @RequestParam String message) {

        try {

            Notification notification =
                    notificationService.createNotification(
                            userId,
                            message
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(notification);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // Get all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>>
    getUserNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService
                        .getUserNotifications(userId)
        );
    }


    // Get unread notifications
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>>
    getUnreadNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService
                        .getUnreadNotifications(userId)
        );
    }


    // Get unread count
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>>
    getUnreadCount(
            @PathVariable Long userId) {

        long count =
                notificationService
                        .getUnreadCount(userId);

        return ResponseEntity.ok(
                Map.of(
                        "unreadCount",
                        count
                )
        );
    }


    // Mark one notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id) {

        try {

            Notification notification =
                    notificationService
                            .markAsRead(id);

            return ResponseEntity.ok(
                    notification
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // Mark all notifications as read
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(
            @PathVariable Long userId) {

        notificationService
                .markAllAsRead(userId);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "All notifications marked as read"
                )
        );
    }


    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long id) {

        try {

            notificationService
                    .deleteNotification(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Notification deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }
}