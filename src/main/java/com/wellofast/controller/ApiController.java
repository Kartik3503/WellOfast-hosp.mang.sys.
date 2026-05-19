package com.wellofast.controller;

import com.wellofast.model.*;
import com.wellofast.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired private UserService userService;
    @Autowired private HospitalService hs;

    private User getUser(Authentication auth) {
        return userService.findByUsername(auth.getName()).orElseThrow();
    }

    // ══════════════════════════════════════════
    //  NOTIFICATION ENDPOINTS
    // ══════════════════════════════════════════

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getNotifications(Authentication auth) {
        User user = getUser(auth);
        List<Notification> all = hs.notificationsByUser(user.getId());
        long unread = hs.unreadNotifCount(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("notifications", all.stream().limit(20).toList());
        result.put("unreadCount", unread);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable String id) {
        hs.markNotifRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notifications/read-all")
    public ResponseEntity<Void> markAllRead(Authentication auth) {
        User user = getUser(auth);
        hs.markAllNotifsRead(user.getId());
        return ResponseEntity.ok().build();
    }

    // ══════════════════════════════════════════
    //  CHAT ENDPOINTS
    // ══════════════════════════════════════════

    @GetMapping("/chat/conversations")
    public ResponseEntity<List<Map<String, Object>>> getConversations(Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(hs.getUserConversations(user.getId()));
    }

    @GetMapping("/chat/{otherUserId}")
    public ResponseEntity<Map<String, Object>> getMessages(@PathVariable String otherUserId, Authentication auth) {
        User user = getUser(auth);
        List<ChatMessage> messages = hs.getConversation(user.getId(), otherUserId);
        String convId = HospitalService.makeConversationId(user.getId(), otherUserId);
        hs.markConversationRead(convId, user.getId());

        User other = userService.findById(otherUserId).orElse(null);
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);
        result.put("otherUser", other != null ? Map.of("id", other.getId(), "name", other.getFullName(), "role", other.getRole()) : null);
        result.put("currentUserId", user.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/chat/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody Map<String, String> payload, Authentication auth) {
        User sender = getUser(auth);
        String receiverId = payload.get("receiverId");
        String message = payload.get("message");

        User receiver = userService.findById(receiverId).orElse(null);
        if (receiver == null || message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ChatMessage msg = hs.sendMessage(sender.getId(), sender.getFullName(), sender.getRole(),
                receiver.getId(), receiver.getFullName(), message.trim());

        // Create notification for receiver
        String link = "PATIENT".equals(receiver.getRole()) ? "/portal/chat/" + sender.getId() : "/doctor/chat/" + sender.getId();
        hs.createNotification(receiver.getId(), "CHAT", "New message from " + sender.getFullName(),
                message.trim().length() > 80 ? message.trim().substring(0, 80) + "..." : message.trim(),
                link, "💬");

        return ResponseEntity.ok(msg);
    }

    @GetMapping("/chat/unread-count")
    public ResponseEntity<Map<String, Long>> unreadChatCount(Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(Map.of("count", hs.unreadChatCount(user.getId())));
    }
}
