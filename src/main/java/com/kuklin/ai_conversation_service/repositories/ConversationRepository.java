package com.kuklin.ai_conversation_service.repositories;

import com.kuklin.ai_conversation_service.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
