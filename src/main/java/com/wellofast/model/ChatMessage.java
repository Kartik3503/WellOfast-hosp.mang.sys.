package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;

    private String senderId;
    private String senderName;
    private String senderRole;      // PATIENT or DOCTOR

    private String receiverId;
    private String receiverName;

    /** Unique conversation key: sorted IDs joined by underscore */
    private String conversationId;

    private String message;
    private boolean read = false;

    private LocalDateTime timestamp;
}
