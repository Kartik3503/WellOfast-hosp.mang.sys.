package com.wellofast.repository;

import com.wellofast.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByConversationIdOrderByTimestampAsc(String conversationId);
    List<ChatMessage> findByReceiverIdAndReadFalse(String receiverId);
    long countByReceiverIdAndReadFalse(String receiverId);

    /** Get latest message per conversation for a user */
    List<ChatMessage> findBySenderIdOrReceiverIdOrderByTimestampDesc(String senderId, String receiverId);
}
