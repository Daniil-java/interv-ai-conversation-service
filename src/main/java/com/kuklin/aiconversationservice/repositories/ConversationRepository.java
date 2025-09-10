package com.kuklin.aiconversationservice.repositories;

import com.kuklin.aiconversationservice.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
