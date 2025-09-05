package com.kuklin.ai_conversation_service.repositories;

import com.kuklin.ai_conversation_service.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = "SELECT * FROM chat_messages c WHERE c.conversation_id = ?1", nativeQuery = true)
    List<ChatMessage> findAllByConversation_Id(Long conversationId);
}
