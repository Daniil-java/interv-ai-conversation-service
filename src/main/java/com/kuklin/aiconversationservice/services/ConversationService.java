package com.kuklin.aiconversationservice.services;

import com.kuklin.aiconversationservice.entities.Conversation;
import com.kuklin.aiconversationservice.models.ConversationDto;
import com.kuklin.aiconversationservice.repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;

    private static final int NAME_MIN_LENGTH = 1;
    private static final int NAME_MAX_LENGTH = 50;
    private static final String ELLIPSIS = "...";
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
        return conversationRepository.findById(id)
                .map(Conversation::entityToDto)
                .orElse(null)
                ;
    }

    public Conversation setNameForConversation(Conversation conversation, String content) {

        return conversationRepository.save(conversation.setName(
                normalizeConversationName(conversation, content)
        ));
    }

    private String normalizeConversationName(Conversation conversation, String name) {
        if (name == null || name.trim().length() < NAME_MIN_LENGTH) {
            name = String.valueOf(conversation.getId());
        }
        if (name.length() > NAME_MAX_LENGTH) {
            name = name.substring(0, NAME_MAX_LENGTH).concat(ELLIPSIS);
        }
        return name;
    }
}
