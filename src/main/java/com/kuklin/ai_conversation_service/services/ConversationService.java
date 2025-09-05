package com.kuklin.ai_conversation_service.services;

import com.kuklin.ai_conversation_service.entities.Conversation;
import com.kuklin.ai_conversation_service.models.ConversationDto;
import com.kuklin.ai_conversation_service.repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationDto getNewConversationDto(ConversationDto conversationDto) {
        return Conversation.entityToDto(
                getNewConversation(conversationDto.getUserId(), conversationDto.getName())
        );
    }
    public Conversation getNewConversation(Long userId) {
        return getNewConversation(userId, null);
    }

    public Conversation getNewConversation(Long userId, String conversationName) {
        return conversationRepository.save(
                new Conversation()
                        .setUserId(userId)
                        .setName(conversationName));
    }

    public Conversation getByIdOrGetNull(Long id) {
        return conversationRepository.findById(id).orElse(null);
    }

    public ConversationDto getConversationDtoByIdOrGetNull(Long id) {
        return Conversation.entityToDto(
                conversationRepository.findById(id).orElse(null)
        );
    }

    public Conversation setNameForConversation(Conversation conversation, String content) {
        return conversationRepository.save(conversation.setName(content));
    }
}
