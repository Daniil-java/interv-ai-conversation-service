package com.kuklin.ai_conversation_service.models.openai;

import com.kuklin.ai_conversation_service.entities.ChatMessage;
import com.kuklin.ai_conversation_service.models.enums.ChatRole;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Message {
    private String role;
    private String content;

    public static List<Message> convertFromChatMessages(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(chatMessage -> new Message()
                        .setRole(chatMessage.getRole().getValue())
                        .setContent(chatMessage.getContent()))
                .toList();
    }

    public static List<Message> getFirstMessage(String content) {
        return List.of(new Message()
                .setContent(content)
                .setRole(ChatRole.USER.getValue())
        );
    }

}
